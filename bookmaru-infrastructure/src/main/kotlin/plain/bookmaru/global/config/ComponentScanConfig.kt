package plain.bookmaru.global.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import plain.bookmaru.common.annotation.ReadOnlyUseCase
import plain.bookmaru.common.annotation.UseCase

@Configuration
@ComponentScan(
    basePackages = ["plain.bookmaru"],
    includeFilters = [
        ComponentScan.Filter(
            type = FilterType.ANNOTATION,
            classes = [
                UseCase::class,
                ReadOnlyUseCase::class
            ]
        )
    ]
)
class ComponentScanConfig {
}