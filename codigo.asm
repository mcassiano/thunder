sseg SEGMENT STACK ;início seg. pilha
byte 16384 DUP(?) ;dimensiona pilha
sseg ENDS ;fim seg. pilha
dseg SEGMENT PUBLIC ;início seg. dados
byte 16384 DUP(?) ;temporários
byte 67 ; const byte 67 em 16386
byte 256 DUP (?) ; var string n em 16384
mov ax, ds:[16386]
mov ds:[16384], ax
dseg ENDS ;fim seg. dados
cseg SEGMENT PUBLIC ;início seg. código
ASSUME CS:cseg, DS:dseg
strt:
mov ax, dseg
mov ds, ax
mov ax, 1 ; move const
mov ds:[0], ax ; move const
mov ax, ds:[0] ; expressao
mov ds:[0], ax ; expressao
mov ax, 4 ; move const
mov ds:[1], ax ; move const
mov al, ds:[0]
mov bl, ds:[1]
add bl, al
mov ds:[0], bl
mov ax, 7 ; move const
mov ds:[2], ax ; move const
mov al, ds:[0]
mov bl, ds:[2]
add bl, al
mov ds:[0], bl
mov ah, 4Ch
int 21h
cseg ENDS ;fim seg. código
END strt ;fim programa
