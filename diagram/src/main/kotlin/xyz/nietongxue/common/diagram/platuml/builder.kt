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


fun UmlBuilder.component(name: String) {
    this.element(name, "component")
}

fun UmlBuilder.entity(name: String) {
    this.element(name, "entity")
}
fun UmlBuilder.file(name: String) {
    this.element(name, "file")
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


fun UmlBuilder.database(name: String, fn: UmlBuilder.() -> Unit) {
    this.container(name, "database", fn)
}

fun UmlBuilder.folder(name: String, fn: UmlBuilder.() -> Unit) {
    this.container(name, "folder", fn)
}
fun UmlBuilder.component(name: String, fn: UmlBuilder.() -> Unit) {
    this.container(name, "component", fn)
}


fun UmlBuilder.`package`(name: String, fn: UmlBuilder.() -> Unit){
    this.container(name, "package", fn)
}


