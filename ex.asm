sseg SEGMENT STACK ;início seg. pilha
byte 16384 DUP(?) ;dimensiona pilha
sseg ENDS ;fim seg. pilha
dseg SEGMENT PUBLIC ;início seg. dados
byte 16384 DUP(?) ;temporários
byte "Matheus$" ; const string n em 16384
dseg ENDS ;fim seg. dados
cseg SEGMENT PUBLIC ;início seg. código
ASSUME CS:cseg, DS:dseg
strt:
mov ax, dseg
mov ds, ax
mov ax, ds:[16384] ; expressao
mov ds:[0], ax ; expressao
mov dx, 0 ; imprime
mov ah, 09h ; imprime
int 21h ; imprime
mov ah, 02h ; new line
mov dl, 0Dh ; new line
int 21h ; new line
mov DL, 0Ah ; new line
int 21h ; new line
mov ah, 4Ch
int 21h
cseg ENDS ;fim seg. código
END strt ;fim programa
