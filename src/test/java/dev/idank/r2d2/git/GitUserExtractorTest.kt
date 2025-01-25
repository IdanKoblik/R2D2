package dev.idank.r2d2.git

import dev.idank.r2d2.BaseTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitUserExtractorTest : BaseTest() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
        GitUserExtractor.invalidateCache()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun `test extract users`() {



    }
}