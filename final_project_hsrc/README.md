compile:
`javac -cp .:json-20210307.jar -d out -sourcepath src/ src/*.java`

execute:
`java -cp .:json-20210307.jar:sqlite-jdbc-3.32.3.2.jar:out/ Main`