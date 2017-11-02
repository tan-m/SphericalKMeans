kmeans:
	javac src/*.java

preprocess:
	python preprocess.py

clean:
	rm src/*.class *.csv *.clabel