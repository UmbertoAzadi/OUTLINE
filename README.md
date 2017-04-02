# CodeSmellDetectorML

How to use: 
-----------

So far the tool allows to learn a machine learning model, serialize the model learn, 
and predict the class of the instances contained in a new dataset.

For learning and serialization are avaiable these flags:
  * -ser `configuration file` for serialize the model learn
  * -print `configuration file` for print at command line the human-readable result of the model learn
  * -save `configuration file` for save the human-readable result of the model learn in a file (one for each classifier chosen)
 
For prediction are avaiable these flags:
  * -pred `configuration file` 
  * -pred `path of the serialized model` `path of the dataset`
  * -pred `path of the dataset` `path of the serialized model`
  
 
 ### Configuration file
 
 There are two type of configuration file, both of them .property :
 
  **Configuration file for serializaton**
  
  dataset = `path of the dataset` (required!)
  
  path = `path where the serialized model will be saved` (optional)
  (if "path" will be not specified or it is incorrect the models will be saved in the folder "result" in the repository)
  
  `name of the ensemble method` `_` `name of the classifier` `_` `everything you want` = [`option`, `option`, ...]
  (if you don't want to specify options, you can just write the name as specified)
  (if you want to specify a classifier without an ensemble method you must put the `_` before the name of the classifier!)
  
  **Configuration file for serializaton**
  
  dataset = `path dataset that contain the instances that have to be predict` (required!)
  
  serialized = `path of the serialized file` (required!)
  
  ### Exemples
  
  * Exemple of a configuration file for serialization:
  
   dataset = C:/Users/uazad/Documents/Progetto/dataset/feature-envy.csv
   path = C:/Users/uazad/Documents/Progetto/_Result
   BOOSTED_J48_unpruned = [-U;]
   Bagging_J48 = [;]
   _J48_reduce_error = [-R]
   _JRip 
   BOOSTED_JRip
   _LibSVM = [-P, 100, -S, 1, -K, 2, -D, 3, -G, 0.0, -R, 0.0, -N, 0.2, -M, 40.0, -C, 1.0, -E, 0.001, -seed, 1]
  
  * Exemple of a configuration file for prediction:
  
   dataset = C:/Users/uazad/Documents/Progetto/dataset/feature-envy.csv
   serialized = C:/Users/uazad/Documents/Progetto/result/5_is_feature_envy_JRip.model
  
  * Exemple of execution
  
  java -jar CSDML_v1.0.jar -ser -save -print C:\Users\uazad\Documents\Progetto\configuration\try_serialization.properties
  
  java -jar CSDML_v1.0.jar -pred C:\Users\uazad\Desktop\università\Stage\Progetto\CodeSmellDetectorML\configuration\try_classification.properties
  
  java -jar CSDML_v1.0.jar -pred C:/Users/uazad/Documents/Progetto/result/5_is_feature_envy_JRip.model C:/Users/uazad/Documents/Progetto/dataset/feature-envy.csv
  
  java -jar CSDML_v1.0.jar -pred C:/Users/uazad/Documents/Progetto/dataset/feature-envy.csv C:/Users/uazad/Documents/Progetto/result/5_is_feature_envy_JRip.model
