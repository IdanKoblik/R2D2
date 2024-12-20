import com.intellij.testFramework.LightPlatformCodeInsightTestCase
import com.intellij.testFramework.builders.ModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl

class MyCustomAnnotatorTest : LightPlatformCodeInsightTestCase() {

    override fun setUp() {
        super.setUp()
    }

    fun testTODOAnnotation() {
        val testCode = """
        fun myMethod() {
            // TODO: Refactor this method
            println("Hello, World!")
        }
        """


    }

}
