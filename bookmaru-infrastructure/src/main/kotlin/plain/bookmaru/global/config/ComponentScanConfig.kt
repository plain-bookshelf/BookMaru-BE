package plain.bookmaru.global.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import plain.bookmaru.common.annotation.ReadOnlyService
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.management.ConcurrencyManager

@Configuration
@ComponentScan(
    basePackages = ["plain.bookmaru"],
    includeFilters = [
        ComponentScan.Filter(
            type = FilterType.ANNOTATION,
            classes = [
                Service::class,
                ReadOnlyService::class,
                ConcurrencyManager::class
            ]
        )
    ]
)
class ComponentScanConfig