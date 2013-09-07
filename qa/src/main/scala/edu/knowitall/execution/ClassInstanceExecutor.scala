package edu.knowitall.execution

import org.slf4j.LoggerFactory
import edu.knowitall.triplestore.TriplestoreClient

case class ClassInstanceExecutor(executor: QueryExecutor) extends QueryExecutor {
 
  import ClassInstanceExecutor.{ciRels, ciRelSetLiteral, ciRelSet}
  
  def expandConjunct(c: TConjunct): List[TConjunct] = {
    val rel = c.values.get(Search.rel).collect { case UnquotedTLiteral(v: String) => v.toLowerCase }
    rel match {
      case None => List(c)
      case Some(litRel) =>
        if (!ciRelSet.contains(litRel)) {
          List(c)
        } else {
          //List(c.copy(values = c.values + (Search.rel -> ciRelSetLiteral)))
          ciRels.map(cir => c.copy(values = c.values + (Search.rel -> QuotedTLiteral(cir))))
        }
    } 
  }
   
  def expandConjQuery(q: ConjunctiveQuery): List[ConjunctiveQuery] = {
    val newCs = q.conjuncts.map(expandConjunct)
    val conjSets = Utils.cartesian[TConjunct](newCs).toList
    conjSets.map(cs => ListConjunctiveQuery(q.qVars, cs.toList))
  }
  
  def expandQuery(uquery: UQuery): List[UQuery] = uquery match {
    case q: ConjunctiveQuery => expandConjQuery(q)
    case _ => List(uquery)
  }
  
  def deriveAnswers(uquery: UQuery): Iterable[AnswerDerivation] = expandQuery(uquery).flatMap(executor.deriveAnswers)
}


object ClassInstanceExecutor {
  
  val ciRels = List("type", "is", 
      "are", 
      "is a kind of", 
      "are a kind of", 
      "is a type of", 
      "are a type of", 
      "is an example of", 
      "are examples of",
      "Instance",
      "Instance Of")
      
  val ciRelSet = ciRels.toSet
      
  val ciRelSetLiteral = SetTLiteral(ciRels.map(QuotedTLiteral(_)))
} 