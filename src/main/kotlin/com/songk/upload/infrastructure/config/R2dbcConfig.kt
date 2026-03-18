package com.songk.upload.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories(basePackages = ["com.songk.upload.adapter.out.persistence"])
class R2dbcConfig
