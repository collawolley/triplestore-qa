package edu.knowitall.scoring.training

import edu.knowitall.tool.conf.BreezeLogisticRegressionTrainer
import edu.knowitall.execution.AnswerGroup
import edu.knowitall.scoring.AnswerGroupFeatures

object AnswerScorerTrainer {
  
  def trainingResource = {
    val url = getClass.getResource("scorer-training.txt")
    require(url != null, "Could not find resource: scorer-training.txt")
    url
  }
  
  def trainer = new BreezeLogisticRegressionTrainer[AnswerGroup](AnswerGroupFeatures.featureSet)
  
  def trainingData = new TrainingDataReader(trainingResource)
  
  def classifier = trainer.train(trainingData)
}