# OUTLINE (tOol for aUtomating daTa anaLysis IN wEka)

Requirements and info: 
-----------

  * this tool was developed and tested with jdk1.8.0
  * if you copy and paste the configuration of the classifier generate by the weka GUI, please make sure to delete 
	"C:\Program Files\Weka-X-X" and the related flag, or check that the path is correct 

For any further information:
-----------
Section 3 of my [bachelor thesis](https://drive.google.com/file/d/0B34r1SFSHGjVVnBxc3lsb3NzOWM/view?usp=sharing)

How to use: 
-----------

So far the tool allows to learn a machine learning model, serialize the model learn, 
predict the class of the instances contained in a new dataset, apply a cross validation,
allow to execute a weka experiment and allow to execute a weka experiment customized to
make it faster.

For learning and serialization are avaiable these flags:
  * -ser `configuration file` for serialize the model learn
  * -print `configuration file` for print at command line the human-readable result of the model learn
  * -save `configuration file` for save the human-readable result of the model learn in a file (one for each classifier chosen)
 
For prediction are avaiable these flags:
  * -pred `configuration file` 
  * -pred `path of the serialized model` `path of the dataset`
  * -pred `path of the dataset` `path of the serialized model`
  
  N.B. the dataset for the prediction __must _not_ contain the class that is supposed to be predected__
  
For cross validation are avaiable these flags:
  * -cross `configuration file`
  * -cross -fold `number` `configuration file`
  * -cross -seed `number` `configuration file`
  * -cross -fold `number` -seed `number` `configuration file` 

  N.B. if the fold or the seed are not specified the default numebers will be used, which are respectively 10 and 1
  
For weka experiment are avaiable these flags:
  * -wekaExp -exptype `classification` -splittype `crossvalidation` -runs `# of runs` -folds `# of cross-validation folds`  `configuration file` 
  * -wekaExp -exptype `regression` -splittype `crossvalidation` -runs `# of runs` -folds `# of cross-validation folds` (-randomized)  `configuration file`      
  * -wekaExp -exptype `classification` -splittype `randomsplit` -runs `# of runs` -percentage `percentage for randomsplit`  `configuration file` 
  * -wekaExp -exptype `regression` -splittype `randomsplit` -runs `# of runs` -percentage `percentage for randomsplit` (-randomized)  `configuration file` 
  
  N.B. if one of the flag are not specified or are incorrect the default values will be used, which are:
       exptype: classification, splittype: crossvalidation, runs: 10, fold: 10, percentage: 66.0, not randomized
    
For the customized implementation of the weka experiment is avaiable this flag:
  * -customExp -runs `# of runs` -folds `# of cross-validation folds`
   
  N.B. This implementation allow to execute the weka experiment only with __classification__ as exptype and __crossvalidation__ as splittype.
       The experiment is performed faster because the classification is executed concurrently, but for this reason if you use too few
       classifier the execution will result slower (because of the synchronizaton points). So it is therefore advisable to use this flag
       if there are specified __at least ten classifier__ in the configuration file, plus it It is strongly recommended to have at least 2GB
       of RAM. If you want to see how it work you can [download the sequence diagram](https://github.com/UmbertoAzadi/OUTLINE/blob/master/docs/CustomExperiment.pdf?raw=true)   
   
 ### Configuration file
 
 ___There are two type of .properties configuration file:___
 
  **Configuration file for serializaton, cross validation and weka experiment**
  
	dataset = <path of the dataset>                          (required)
	path = <path where all the files will be saved>          (optional)
  	
  	key = <name of the classifier options of the classifier> (required at least one classifier)
	
N.B. the names are inteded as the name __complete with the path__ of the weka class that corrispond with the classifier 
     you want to use (see [weka documetation](http://weka.sourceforge.net/doc.stable/)). You can also copy and paste the
     configuration of the classifier generate by the weka GUI.
     if "path" will be not specified or it is incorrect the models will be saved in the folder "result" in the repository
  
  **Configuration file for prediction**
  
  	dataset = <path dataset that contain the instances that have to be predict> (required!)
	labels = <list of class labels>
  	key =     <path of the serialized file>                                     (required at least one)
       
      
			
  
  ### Examples
  
  **Example of a .properties configuration file for serialization:**
  
	dataset = C:/Users/uazad/Documents/Progetto/dataset/feature-envy.csv
 
	J48_unpruned = weka.classifiers.meta.AdaBoostM1 -I 2 -W weka.classifiers.trees.J48 -- -U
	weka.classifiers.meta.Bagging -W weka.classifiers.trees.J48
	weka.classifiers.trees.J48 -R
	weka.classifiers.rules.JRip 
	weka.classifiers.meta.AdaBoostM1 -W weka.classifiers.rules.JRip
	weka.classifiers.functions.LibSVM -P 100 -S 1 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.2 -M 40.0 -C 1.0 -E 0.001 -seed 1
  
  **Example of a .properties configuration file for prediction:**
  
	dataset: C:/Users/uazad/Documents/Progetto/dataset/feature-envy.csv
	labels = true,false
	key1 = C:/Users/uazad/Documents/Progetto/result/5_is_feature_envy_JRip.model
	key2 = C:/Users/uazad/Documents/Progetto/result/1_is_feature_envy_AdaBoostM1_J48.model

  
  **Examples of execution**
  
	java -jar OUTLINE_v1.1.jar -ser -save -print .\configuration\try_classification.properties
  
	java -jar OUTLINE_v1.1.jar -pred .\configuration\try_prediction.properties
  
	java -jar OUTLINE_v1.1.jar -pred .\result\5_is_feature_envy_J48.model .\dataset\feature-envy.csv
  
	java -jar OUTLINE_v1.1.jar -pred .\dataset\feature-envy.csv .\result\5_is_feature_envy_J48.model
   
	java -jar OUTLINE_v1.1.jar -cross -seed 2 -fold 8 .\configuration\try_classification.properties
   
	java -jar OUTLINE_v1.1.jar -wekaExp -exptype classification -splittype crossvalidation -runs 8 -folds 8  .\configuration\try_classification.properties
   
	java -jar OUTLINE_v1.1.jar -wekaExp -exptype regression -splittype randomsplit -runs 6 -percentage 80.0 -randomized .\configuration\try_regression.properties
  
	java -jar OUTLINE_v1.1.jar -customExp -fold 10 -runs 10 .\configuration\try_classification.properties
