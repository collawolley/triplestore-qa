package edu.knowitall.search.qa

import edu.knowitall.execution.ExecTuple

case class AnswerState(answer: String, execTuple: ExecTuple) extends QaState