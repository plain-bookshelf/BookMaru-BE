package plain.bookmaru.domain.display.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.book.exception.NotFoundBookException
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
import plain.bookmaru.domain.book.vo.BookInfo
import plain.bookmaru.domain.community.port.out.CommentPort
import plain.bookmaru.domain.display.port.`in`.ViewBookDetailPageCommentUseCase
import plain.bookmaru.domain.display.port.`in`.ViewBookDetailPageUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewBookDetailPageCommand
import plain.bookmaru.domain.display.port.`in`.command.ViewBookDetailPageCommentCommand
import plain.bookmaru.domain.display.port.out.result.BookDetailPageResult
import plain.bookmaru.domain.display.port.out.result.CommentResult

@Service
class ViewBookDetailPageService(
    private val bookAffiliationPort: BookAffiliationPort,
    private val commentPort: CommentPort
) : ViewBookDetailPageUseCase, ViewBookDetailPageCommentUseCase {
    override suspend fun bookDetailExecute(command: ViewBookDetailPageCommand): BookDetailPageResult {
        val bookInfo = bookAffiliationPort.findBookInfoByBookId(command.bookAffiliationId, command.affiliationId)
            ?: throw NotFoundBookException("${command.bookAffiliationId} 아이디를 가진 책 정보를 찾지 못 했습니다.")

        val bookDetailInfo = bookInfo.book.bookInfo

        return BookDetailPageResult(
            BookInfo(
                affiliationName = bookInfo.affiliationName,
                title = bookDetailInfo.title,
                author = bookDetailInfo.author,
                publicationDate = bookDetailInfo.publicationDate,
                introduction = bookDetailInfo.introduction,
                bookImage = bookDetailInfo.bookImage,
                publisher = bookDetailInfo.publisher,
            ),
            isEnableRental = bookInfo.availableCount != 0
        )
    }

    override suspend fun commentExecute(command: ViewBookDetailPageCommentCommand): SliceResult<CommentResult> {
        val commentList = commentPort.findByBookAffiliationId(command.bookAffiliationId, command.pageCommand)

        return SliceResult(
            commentList.content,
            commentList.isLastPage
        )
    }
}