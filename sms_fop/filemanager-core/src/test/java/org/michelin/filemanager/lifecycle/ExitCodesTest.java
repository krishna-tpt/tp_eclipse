package org.michelin.filemanager.lifecycle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sanity: exit code values match the README contract.
 * Class name retained to avoid renaming churn; subject is the renamed ExitCode enum.
 */
class ExitCodesTest {

    @Test
    @DisplayName("Exit codes match documented contract (0,1,2,3,4)")
    void exitCodesMatchContract() {
        assertThat(ExitCode.SUCCESS.code()).isEqualTo(0);
        assertThat(ExitCode.CONFIG_ERROR.code()).isEqualTo(1);
        assertThat(ExitCode.DB_ERROR.code()).isEqualTo(2);
        assertThat(ExitCode.FILE_SOURCE_ERROR.code()).isEqualTo(3);
        assertThat(ExitCode.PARTIAL_FAILURE.code()).isEqualTo(4);
    }
}
