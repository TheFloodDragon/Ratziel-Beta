package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.Workspace
import cn.fd.ratziel.common.element.registry.ElementRegistry
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementConfiguration
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.element.ElementType
import cn.fd.ratziel.core.element.FILE_NAME_ELEMENT_NAME
import cn.fd.ratziel.core.element.elementName
import cn.fd.ratziel.core.element.elementType
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ElementLoaderTest {

    private val testType = ElementType("loader-test", "demo")
    private val testHandler = object : ElementHandler {
        override suspend fun handle(elements: Collection<Element>) = Unit
    }

    @BeforeTest
    fun setUp() {
        ElementRegistry.register(testType, testHandler)
    }

    @AfterTest
    fun tearDown() {
        ElementRegistry.unregister(testType)
    }

    @Test
    fun `parses legacy multi-element format when no internal configuration is provided`() {
        val json = Json.parseToJsonElement(
            """
            {
              "first": {"loader-test:demo": {"value": 1}},
              "second": {"loader-test:demo": {"value": 2}}
            }
            """.trimIndent()
        )

        val elements = DefaultElementLoader.parseElements(workspace(), json, File("legacy.yml"))

        assertEquals(listOf("first", "second"), elements.map { it.name })
        assertTrue(elements.all { it.type == testType })
        assertEquals(Json.parseToJsonElement("{" + "\"value\":1" + "}"), elements[0].property)
        assertEquals(Json.parseToJsonElement("{" + "\"value\":2" + "}"), elements[1].property)
    }

    @Test
    fun `parses shared element type from internal configuration`() {
        val json = Json.parseToJsonElement(
            """
            {
              "internal": {"elementType": "loader-test:demo"},
              "first": {"value": 1},
              "second": {"value": 2}
            }
            """.trimIndent()
        )

        val elements = DefaultElementLoader.parseElements(workspace(), json, File("shared-type.yml"))

        assertEquals(listOf("first", "second"), elements.map { it.name })
        assertTrue(elements.all { it.type == testType })
        assertEquals(Json.parseToJsonElement("{" + "\"value\":1" + "}"), elements[0].property)
        assertEquals(Json.parseToJsonElement("{" + "\"value\":2" + "}"), elements[1].property)
    }

    @Test
    fun `parses whole file as a single element when internal name and type are provided`() {
        val json = Json.parseToJsonElement(
            """
            {
              "internal": {
                "elementName": "merged",
                "elementType": "loader-test:demo"
              },
              "value": 1,
              "nested": {"enabled": true}
            }
            """.trimIndent()
        )

        val elements = DefaultElementLoader.parseElements(workspace(), json, File("single.yml"))

        assertEquals(1, elements.size)
        assertEquals("merged", elements.single().name)
        assertEquals(testType, elements.single().type)
        assertEquals(
            Json.parseToJsonElement(
                """
                {
                  "value": 1,
                  "nested": {"enabled": true}
                }
                """.trimIndent()
            ),
            elements.single().property
        )
    }

    @Test
    fun `applies workspace default element type when file has no internal configuration`() {
        val json = Json.parseToJsonElement(
            """
            {
              "first": {"value": 1},
              "second": {"value": 2}
            }
            """.trimIndent()
        )

        val elements = DefaultElementLoader.parseElements(
            workspace {
                ElementConfiguration.elementType("loader-test:demo")
            },
            json,
            File("workspace-type.yml")
        )

        assertEquals(listOf("first", "second"), elements.map { it.name })
        assertTrue(elements.all { it.type == testType })
    }

    @Test
    fun `applies workspace default file name element strategy`() {
        val json = Json.parseToJsonElement(
            """
            {
              "value": 1,
              "nested": {"enabled": true}
            }
            """.trimIndent()
        )

        val elements = DefaultElementLoader.parseElements(
            workspace {
                ElementConfiguration.elementName(FILE_NAME_ELEMENT_NAME)
                ElementConfiguration.elementType("loader-test:demo")
            },
            json,
            File("workspace-default.yml")
        )

        assertEquals(1, elements.size)
        assertEquals("workspace-default", elements.single().name)
        assertEquals(testType, elements.single().type)
    }

    @Test
    fun `internal configuration overrides workspace defaults`() {
        val json = Json.parseToJsonElement(
            """
            {
              "internal": {
                "elementName": "override",
                "elementType": "loader-test:demo"
              },
              "value": 1
            }
            """.trimIndent()
        )

        val elements = DefaultElementLoader.parseElements(
            workspace {
                ElementConfiguration.elementName(FILE_NAME_ELEMENT_NAME)
                ElementConfiguration.elementType("loader-test:demo")
            },
            json,
            File("workspace-override.yml")
        )

        assertEquals(1, elements.size)
        assertEquals("override", elements.single().name)
        assertEquals(testType, elements.single().type)
    }

    @Test
    fun `normalizes none element type in internal configuration and falls back to legacy format`() {
        val json = Json.parseToJsonElement(
            """
            {
              "internal": {"elementType": "None"},
              "first": {"loader-test:demo": {"value": 1}}
            }
            """.trimIndent()
        )

        val elements = DefaultElementLoader.parseElements(
            workspace {
                ElementConfiguration.elementType("loader-test:demo")
            },
            json,
            File("workspace-none.yml")
        )

        assertEquals(1, elements.size)
        assertEquals("first", elements.single().name)
        assertEquals(testType, elements.single().type)
        assertEquals(Json.parseToJsonElement("{" + "\"value\":1" + "}"), elements.single().property)
    }

    @Test
    fun `rejects workspace element name values other than file name placeholder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            workspace {
                ElementConfiguration.elementName("merged")
            }
        }

        assertTrue(exception.message?.contains(FILE_NAME_ELEMENT_NAME) == true)
    }

    private fun workspace(builder: ElementConfiguration.Builder.() -> Unit = {}): Workspace {
        return Workspace(File("."), ElementConfiguration(builder = builder))
    }
}
