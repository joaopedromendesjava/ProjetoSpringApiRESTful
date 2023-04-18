import { Profissao } from "./Profissao";
import { Telefone } from "./telefone";

export class User {

    id: number;
    login: string;
    nome: string;
    cpf: string;
    senha : string;
    dataNascimento : string;
    profissao : Profissao = new Profissao();
    salario : DoubleRange;
    telefones : Array<Telefone> ;

}




