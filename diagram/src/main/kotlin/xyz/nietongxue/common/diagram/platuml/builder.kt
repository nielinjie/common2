package xyz.nietongxue.common.diagram.platuml


/*
actor actor
agent agent
artifact artifact
boundary boundary
card card
cloud cloud
component component
control control
database database
entity entity
file file
folder folder
frame frame
interface  interface
node node
package package
queue queue
stack stack
rectangle rectangle
storage storage
usecase usecase
 */


fun UmlElementsBuilder.component(name: String, appearanceBuilding: AppearanceBuilder.() -> Unit = {}) {
    this.element(name, "component", appearanceBuilding)
}

fun UmlElementsBuilder.entity(name: String, appearanceBuilding: AppearanceBuilder.() -> Unit = {}) {
    this.element(name, "entity", appearanceBuilding)
}

fun UmlElementsBuilder.file(name: String, appearanceBuilding: AppearanceBuilder.() -> Unit = {}) {
    this.element(name, "file", appearanceBuilding)
}

/*

可嵌套的

artifact artifact 
card card 
cloud cloud
component component
database database
file file
folder folder
frame frame
hexagon hexagon
node node
package package
queue queue
rectangle rectangle
stack stack
storage storage
 */


fun UmlElementsBuilder.database(
    name: String,
    appearanceBuilding: AppearanceBuilder.() -> Unit = {},
    fn: UmlElementsBuilder.() -> Unit,
) {
    this.container(name = name, type = "database", appearanceBuilding, fn = fn)
}

fun UmlElementsBuilder.folder(
    name: String,
    appearanceBuilding: AppearanceBuilder.() -> Unit = {}, fn: UmlElementsBuilder.() -> Unit,

    ) {
    this.container(name, "folder", appearanceBuilding, fn = fn)
}

fun UmlElementsBuilder.component(
    name: String,
    appearanceBuilding: AppearanceBuilder.() -> Unit = {},
    fn: UmlElementsBuilder.() -> Unit
) {
    this.container(name, "component", appearanceBuilding, fn = fn)
}


fun UmlElementsBuilder.`package`(
    name: String,
    appearanceBuilding: AppearanceBuilder.() -> Unit = {},
    fn: UmlElementsBuilder.() -> Unit
) {
    this.container(name, "package", appearanceBuilding, fn = fn)
}


