package xyz.nietongxue.common.scripts


import xyz.nietongxue.common.query.Query

def filterString = "[{and:{name:{eq:alice}}}]"
def filterJson = xyz.nietongxue.common.json.autoParse.ParseKt.autoParse(filterString)
def filter = xyz.nietongxue.common.query.JsonKt.jsonToFilter(filterJson.first)
def query = new Query(filter,null,null)
query.toString()