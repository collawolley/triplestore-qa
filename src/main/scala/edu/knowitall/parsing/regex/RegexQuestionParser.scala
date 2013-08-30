package edu.knowitall.parsing.regex

import edu.knowitall.tool.chunk.ChunkedToken
import edu.knowitall.tool.stem.Lemmatized
import edu.washington.cs.knowitall.regex
import edu.washington.cs.knowitall.regex.Match
import edu.washington.cs.knowitall.regex.RegularExpression
import edu.washington.cs.knowitall.regex.ExpressionFactory
import edu.washington.cs.knowitall.regex.Expression
import edu.knowitall.taggers.tag.PatternTagger.makeRegex
import edu.knowitall.execution.UQuery
import edu.knowitall.execution.ListConjunctiveQuery
import edu.knowitall.parsing.QuestionParser
import edu.knowitall.execution.UnquotedTLiteral
import edu.knowitall.execution.TVariable
import edu.knowitall.execution.Search.Field
import edu.knowitall.execution.TVal
import edu.knowitall.execution.TConjunct
import edu.knowitall.execution.Search.{arg1, rel, arg2}
import edu.knowitall.tool.stem.MorphaStemmer
import edu.knowitall.tool.chunk.OpenNlpChunker
import scala.collection.JavaConversions._


case class RegexQuestionParser() extends QuestionParser {
  
  import RegexQuestionPatterns.patterns
  
  def parse(question: String) = patterns.flatMap(p => p.parse(question)) 
}

object RegexQuestionParserRepl extends App {
  
  lazy val parser = new RegexQuestionParser
  
  io.Source.stdin.getLines.foreach { q =>
    val queries = parser.parse(q)
    if (queries.isEmpty) println("No match")
    queries.foreach { query =>
      println(query)  
    }
  }
  
}

object AnalyzeRegexQuestionParser extends App { 
  
  import RegexQuestionPatterns.patterns
  import edu.knowitall.scoring.training.WikiAnswersSampler
  
  val maxQs = 50
  
  val random = new scala.util.Random(0)
  
  val wikiAnswersData = args(0)
  
  val wikiSampler = new WikiAnswersSampler(wikiAnswersData)
  
  val matched = wikiSampler.flatMap(questionSet =>
    questionSet.flatMap({ question =>
      patterns.flatMap(_.parse(question).headOption).map(query => (question ,query))
    }).headOption
  )
 
  println(matched.size)
  
  matched.take(100).foreach { case (question, query) => println(question + "\t" + query)}
}