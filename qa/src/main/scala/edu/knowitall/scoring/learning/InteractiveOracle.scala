package edu.knowitall.scoring.learning

import edu.knowitall.apps.AnswerDerivation
import edu.knowitall.util.TuplePrinter.printTuple
import edu.knowitall.execution.Tabulator.{format => toTable}

class InteractiveOracle extends CorrectnessModel[String, AnswerDerivation] {
  
  private def parseInt(s: String) = 
    try Some(s.toInt)
    catch {
      case e: Throwable => None
    }
  
  private def parseInts(s: String) =
    try s.split("""[^-0-9]+""").map(_.toInt).toSeq
    catch {
      case e: Throwable => Nil
    }
  
  private def derivString(deriv: AnswerDerivation) = 
    printTuple(deriv.execTuple.tuple)
    
  private def derivsToTable(derivs: Seq[AnswerDerivation]) = {
    val paired = derivs.zipWithIndex map { case (a, b) => List(b, a.answerString, derivString(a)) }
    toTable(List("#", "answer", "evidence") +: paired)
  }
  
  private def getBoolean: Boolean = {
    print("> ")
    Console.readLine match {
      case "1" => true
      case "-1" => false
      case "0" => false
      case _ => {
        println("Type 1 for true, 0 or -1 for false")
        getBoolean
      }
    }
  }
  
  private def getInt(min: Int, max: Int): Int = {
    print("> ")
    parseInt(Console.readLine) match {
      case Some(i) if min <= i && i <= max => i
      case Some(i) => {
        println(s"Must choose between $min and $max")
        getInt(min, max)
      }
      case None => {
        println(s"Must enter an integer")
        getInt(min, max)
      }
    }
  }
  
  private def getInts(min: Int, max: Int): List[Int] = {
    print("> ")
    parseInts(Console.readLine) match {
      case Nil => {
        println(s"Must enter a list of integers")
        getInts(min, max)
      }
      case lst if lst.contains(-1) && lst.size > 1 => {
        println("Cannot have -1 in list")
        getInts(min, max)
      }
      case lst if lst.forall(i => min <= i && i <= max) => lst.toList
      case _ => {
        println(s"Must enter a list of integers between $min and $max")
        getInts(min, max)
      }
    }
  }
  
  def pickCorrectMultiple(question: String, derivs: Seq[AnswerDerivation]) = 
    if (derivs.size > 0) {
      val iderivs = derivs.toIndexedSeq 
      println(s"Question = $question\n")
      println(s"Pick from derivations below (-1 means no answer)")
      println(derivsToTable(derivs))
      getInts(-1, derivs.size-1) match {
        case List(-1) => Nil
        case lst => lst.map(iderivs(_))
      } 
    } else {
      Nil
    }
  
  override def isCorrect(question: String, deriv: AnswerDerivation) = {
    println(s"Question = $question")
    println(s"Answer = ${deriv.answerString}")
    println(s"Derivation = ${derivString(deriv)}")
    println()
    getBoolean
  }
  
  override def pickCorrect(question: String, derivs: Seq[AnswerDerivation]) = 
   if (derivs.size > 0) {
    val iderivs = derivs.toIndexedSeq 
    println(s"Question = $question\n")
    println(s"Pick from derivations below (-1 means no answer)")
    println(derivsToTable(derivs))
    getInt(-1, derivs.size-1) match {
      case -1 => None
      case i => Some(iderivs(i))
    }
  } else None

}