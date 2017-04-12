package it.unimib.disco.essere.yaml;

import java.util.Arrays;

public class Classifiers{
	public String ensemble;
	public String name;
	public String[] options;
	public String[] ens_options;
	public String comment;
	

	
	@Override
	public String toString() {
		return "Classifiers [ensemble=" + ensemble + ", name=" + name + ", options=" + Arrays.toString(options)
				+ ", ens_options=" + Arrays.toString(ens_options) + ", comment=" + comment + "]";
	}
}
