

## 可能的 element

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

## 可能的 container

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

## 可能的 connector

extension <|--
implementation <|..
composition *--
aggregation o--
dependency -->
association ..>
dashedLine ..
solidLine --
arrow -->

扩展 (Extension)	<|--	类在层次结构中的特化 (Specialization of classes in hierarchy)
实现 (Implementation)	<|..	通过类实现接口 (Implementation of interface by class)
构成 (Composition)	*--	没有整体就没有部分 (No part without whole)
聚合 (Aggregation)	o--	部分可以独立于整体而存在 (Part can exist independently from whole)
依赖性 (Dependency)	-->	对象使用另一个对象 (Object uses another object)
依赖 (Association)	..>	一种较弱的依赖形式 (A weaker form of dependency)

Elements can be connected using combinations of dashed lines (..), solid lines (--), and arrows (-->).
