# thunder
compiler wrote for the l language, a fictional language described in our compilers class @ puc minas

Commands to compile on windows
Do this in the root project folder:

dir /s /B *.java > sources.txt

javac @sources.txt

java -cp src me.cassiano.thunder.Main  ex.l saida.txt
