package plain.bookmaru.domain.member.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.member.exception.errorcode.MemberErrorCode

class RentalOrReservationBookExistException : BaseException(MemberErrorCode.RENTAL_OR_RESERVATION_BOOK_EXIST, "")