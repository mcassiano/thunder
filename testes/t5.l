/* Exemplo de Programa na linguagem L */

int n<-0x11;
string nome;
boolean naoTerminou;
final MAXITER<-10;

/* Programa Principal */

write("Digite seu nome: ");
readln(nome);
naoTerminou<-TRUE;
n<-0;
while (naoTerminou) begin
  writeln("Ola' ",nome);
  n<-n+1;
  naoTerminou<-n<MAXITER;
endwhile

