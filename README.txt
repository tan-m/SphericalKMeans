The purpose of this project is to write a clustering algorithm based on spherical k-means for clustering objects corresponding to sparse high dimensional vectors. The project consists of multiple components that involve getting the dataset, selecting the subset to cluster, pre-processing the dataset to convert it into a sparse representation, clustering the dataset, and evaluating the quality of the clustering solution.

The dataset for this is derived from the "Reuters-21578 Text Categorization Collection Data Set" that is available at the UCI Machine Learning Repository

The various SGML files (.sgm) extension are processed and the articles that contain only a single topic select.

The preprocessing consists of the following:
Eliminating any non-ascii characters.
Changing the character case to lower-case.
Replacing any non alphanumeric characters with space.
Spliting the text into tokens, using space as the delimiter.
Eliminating any tokens that contain only digits or the tokens that occur in less than 5 articles. 

All the tokens that remained after step 5 (above) across all articles are collected and used to represent each article as a frequency vector in the distinct token space.

The clustering program outputs a two dimensional matrix of dimensions (# of clusters)*(# of classes) whose entries are the number of objects of a particular class that belongs to a particular cluster

To run the clustering program:

java SphKmeans input-file class-file #clusters #trials output-file


