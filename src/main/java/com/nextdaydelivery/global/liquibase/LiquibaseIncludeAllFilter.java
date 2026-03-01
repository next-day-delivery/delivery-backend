package com.nextdaydelivery.global.liquibase;

import liquibase.changelog.IncludeAllFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * LiquibaseIncludeAllFilter
 * [허용 패턴]
 * - 형식: {숫자}_{설명}.sql
 * - 예시: 001_create_users.sql, 02_add_column.sql, 1_init.sql
 */
@Slf4j
public class LiquibaseIncludeAllFilter implements IncludeAllFilter {
    private static final Pattern SEQUENCE_PATTERN = Pattern.compile("^[0-9]+_[a-zA-Z0-9_]+\\.sql$");

    @Override
    public boolean include(String changeLogPath) {
        if (!StringUtils.hasText(changeLogPath)) {
            return false;
        }

        String fileName = Paths.get(changeLogPath).getFileName().toString();

        if (fileName.startsWith(".")) {
            return false;
        }

        boolean matches = SEQUENCE_PATTERN.matcher(fileName).matches();

        if (!matches) {
            log.debug("[Liquibase] Skipped file (Invalid Name): {} | Path: {}", fileName, changeLogPath);
        }

        return matches;
    }
}
