// Grammar file for Parser deliverable
// CSCE 315-502 Team 12: Nicholas Charchenko, Stanley Chen, Jonathan Chapa
// UIN 524003968

/* Note that the ANTLR jar file is already in this directory! */
grammar Grammar;

query : relation_name '<-' expr ';'	;

relation_name : identifier ;

identifier : Alpha ( Alpha | Digit )* ;

//Expressions representing the six operations in relational algebra
expr : atomic_expr 
             | selection
             | projection
             | renaming
             | union
             | difference
             | product
;

atomic_expr : relation_name | '(' expr ')'	;

selection : 'select' '(' condition ')' atomic_expr	;

condition : conjunction ( '||' conjunction )*	;

conjunction : comparison ( '&&' comparison )*	;

comparison : operand Op operand
             | '(' condition ')'
;

operand : attribute_name | literal	;

attribute_name : identifier	;

// Both string and number literals
literal : '"' (Alpha | Digit)* '"'
		| Digit+						
;

projection : 'project' '(' attribute_list ')' atomic_expr	;

attribute_list : attribute_name ( ',' attribute_name )* ;

renaming : 'rename' '(' attribute_list ')' atomic_expr	;

union : atomic_expr '+' atomic_expr	;

difference : atomic_expr '-' atomic_expr	;

product : atomic_expr '*' atomic_expr 	;


command : (open_cmd | close_cmd | write_cmd | exit_cmd | show_cmd
	| create_cmd | update_cmd | insert_cmd | delete_cmd) ';'
;

// These commands come from SQL queries

open_cmd : 'OPEN' relation_name	;

close_cmd : 'CLOSE' relation_name 	;

write_cmd : 'WRITE' relation_name 	;

exit_cmd : 'EXIT'	;

show_cmd : 'SHOW' atomic_expr 	;

create_cmd : 'CREATE TABLE' relation_name '(' typed_attribute_list ')'
	'PRIMARY KEY' '(' attribute_list ')'
;

update_cmd : 'UPDATE' relation_name 'SET' attribute_name '=' literal ( ',' 
	attribute_name '=' literal )*	'WHERE' condition 
;

insert_cmd : 'INSERT INTO' relation_name 'VALUES FROM' '(' literal ( ',' 
	literal )* ')' | 'INSERT INTO' relation_name 'VALUES FROM RELATION' expr
;

delete_cmd : 'DELETE FROM' relation_name 'WHERE' condition	;

typed_attribute_list : attribute_name type ( ',' attribute_name type )*	;

type : 'VARCHAR' '(' integer ')'  | 'INTEGER'	;

integer : Digit ( Digit )*	;

program : ( query | command )* 	;

/* The following are lexical tokens */

// Boolean operations
Op : '==' | '!=' | '<' | '>' | '<=' | '>='	;

// Whitespace
WS : [ \t\r\n]+ -> skip;

Alpha : [a-zA-Z_] ;

Digit : [0-9] ;
