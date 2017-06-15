package it.unimib.disco.essere.experiment;


import weka.core.Instance;
import weka.experiment.PairedTTester;

public class CustomPrintPairedTTester extends PairedTTester{
	
	/**  for serialization */
	private static final long serialVersionUID = 1L;

	/**
	 * Returns a string descriptive of the key column values for the "datasets
	 * 
	 * @param template the template
	 * @return a value of type 'String'
	 */
	@Override
	protected String templateString(Instance template) {

		String result = "";
		for (int m_DatasetKeyColumn : m_DatasetKeyColumns) {
			result += template.toString(m_DatasetKeyColumn) + ' ';
		}
		
		System.out.println(result);
		
		if (result.contains("\'")) {
			String[] names = result.split("\'");
			result = names[0].substring(names[0].lastIndexOf('.') + 1);
			if(names[1].contains("weka.classifier")){
				int index = names[1].indexOf("weka.classifier") + "weka.classifier".length() + 2;
				String tmp = names[1].substring(index);
				result += tmp.substring(tmp.indexOf(".") + 1).replace(" --", "");
			}else{
				result += names[1];
			}
		}else{
			if (result.startsWith("weka.classifiers.")) {
				result = result.substring("weka.classifiers.".length());
			}
		}
		
		return result.trim();
	}
	
	
	/**
	 * clears the content and fills the column and row names according to the
	 * given sorting
	 */
	@Override
	protected void initResultMatrix() {
		/*m_ResultMatrix.setSize(getNumResultsets(), getNumDatasets());
		m_ResultMatrix.setShowStdDev(m_ShowStdDevs);

		for (int i = 0; i < getNumDatasets(); i++) {
			m_ResultMatrix.setRowName(i,
					templateString(m_DatasetSpecifiers.specifier(i)));
		}

		for (int j = 0; j < getNumResultsets(); j++) {
			m_ResultMatrix.setColName(j, getResultsetName(j));
			m_ResultMatrix.setColHidden(j, !displayResultset(j));
		}
		*
		*/
		super.initResultMatrix();
		this.setFoldColumn(this.m_FoldColumn + 1);
		
		
	}
}
