package com.ti2cc;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import spark.Request;
import spark.Response;
import spark.Route;

public class Principal {
	
	public static void main(String[] args) {
		port(9393);
		final DAO dao = new DAO();
		
		dao.conectar();
		
		
		//get("/denuncias", (request, response) -> denuncias.getAll(request, response));
		//post("/buscarCidadesDoEstado", (request, response) -> dao.buscarCidadesDoEstado(request, response));
		get("/test", new Route() {
			public Object handle(Request request, Response response) throws Exception {
				return "hello";
			}
		});
		
		post("/buscarCidadesDoEstado", new Route() {
			public Object handle(Request request, Response response) throws Exception {
				return dao.buscarCidadesDoEstado(request, response);
			}
		});
		
		//post("/buscarCandidadosPorCidade", (request, response) -> dao.buscarCandidadosPorCidade(request, response));
		//post("/login", (request, response) -> dao.login(request, response));
		//post("/adicionarUsuario", (request, response) -> dao.adicionarUsuario(request, response));
		//post("/avaliar", (request, response) -> dao.tentativaAvaliacao(request, response));
		//post("/buscarComentarios", (request, response) -> dao.buscarComentarios(request, response));
		//get("/allCandidatura", (request, response) -> dao.getCandidaturas(request, response));
		 
		
		
		
		
		
		
		
		
		
		/*
		//inserir denuncia
		Denuncia denuncia = new Denuncia(6, "bolsonaro � hetero", 1, 3);
		if(dao.inserirDenuncia(denuncia)== true) {
			System.out.println("Denuncia Inserido com sucesso -> " + denuncia.toString());
		}
		
		//atualizar denuncia
		denuncia.setAcusado("fake nao � fake");
		dao.atualizarDenuncia(denuncia);
		*/
		
		//excluir denuncia pelo id
		//dao.excluirDenuncia(5);
		

		
	
		 
		//mostrar denuncias
		/*Denuncia[] denuncias = dao.getDenuncias();
		System.out.println("==== Mostrar usuários === ");		
		for(int i = 0; i < denuncias.length; i++) {
			System.out.println(denuncias[i].toString());
		}*/
		
		/*
		//Inserir um elemento na tabela
		Usuario usuario = new Usuario(12, "pablo", "pablo",'M');
		if(dao.inserirUsuario(usuario) == true) {
			System.out.println("Inserido com sucesso -> " + usuario.toString());
		}
		
		//Mostrar usuarios do sexo masculino		
		System.out.println("==== Mostrar usuarios do sexo masculino === ");
		Usuario[] usuarios = dao.getUsuariosMasculinos();
		for(int i = 0; i < usuarios.length; i++) {
			System.out.println(usuarios[i].toString());
		}

		//Atualizar usuário
		usuario.setSenha("nova senha");
		dao.atualizarUsuario(usuario);

		//Mostrar usuários do sexo masculino
		System.out.println("==== Mostrar usuários === ");
		usuarios = dao.getUsuarios();
		for(int i = 0; i < usuarios.length; i++) {
			System.out.println(usuarios[i].toString());
		}
		
		//Excluir usuário
		dao.excluirUsuario(usuario.getCodigo());
		
		//Mostrar usuários
		usuarios = dao.getUsuarios();
		System.out.println("==== Mostrar usuários === ");		
		for(int i = 0; i < usuarios.length; i++) {
			System.out.println(usuarios[i].toString());
		} */
		
		//dao.close();
	}
}
