package xyz.nietongxue.common.schema


/**
 *     private List<Parameter> parameters = null;
 *     private RequestBody requestBody = null;
 *     private ApiResponses responses = null;
 */

data class Operation(
    val request: DataSchema? = null,
    val response: DataSchema? = null,
    val parameters: List<Parameter> = emptyList(),
)

/**
 * public enum StyleEnum {
 *         MATRIX("matrix"),
 *         LABEL("label"),
 *         FORM("form"),
 *         SIMPLE("simple"),
 *         SPACEDELIMITED("spaceDelimited"),
 *         PIPEDELIMITED("pipeDelimited"),
 *         DEEPOBJECT("deepObject");
 *     }
 */


data class Parameter(
    val name: String,
    /**
     * The location of the parameter. Possible values are "query", "header", "path" or "cookie".
     */
    val schema: DataSchema,
    val `in`: String = "query",
    val required: Boolean = false,
    val style: String = "form"
)