/* inicio do arquivo */

final oi <- 1000;
final nome <- "nome";
string nome2 <- "nome";

boolean x, y;
x <- FALSE;
x <- TRUE;

readln(nome2);


write(nome2);

writeln("Ola' ",nome);
while (x) x <- (x = TRUE);

while (x) begin
  writeln("Ola' ",nome);
  x<-FALSE;
endwhile

while (x) begin
  if (x) begin
  x<-FALSE;
endif
else begin
while (x) begin
    x<-FALSE;
endwhile
endelse
endwhile

if (x) x<-1;
else x<-2;