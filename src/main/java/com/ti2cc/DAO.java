package com.ti2cc;

import java.sql.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Request;
import spark.Response;


public class DAO {
	private Connection conexao;
	
	public DAO() {
		conexao = null;
	}
	
	public boolean conectar() {
		String driverName = "org.postgresql.Driver";                    
		String serverName = "pucmgti02.postgres.database.azure.com";
		String mydatabase = "postgres";
		int porta = 5432;
		String url = "jdbc:postgresql://" + serverName + ":" + porta +"/" + mydatabase+"?gssEncMode=disable";
		String username = "adm@pucmgti02";
		String password = "@Pucminas";
		boolean status = false;
 
		try {
			Class.forName(driverName);
			conexao = DriverManager.getConnection(url, username, password);
			status = (conexao == null);
			System.out.println("Conexão efetuada com o postgres!");
		} catch (ClassNotFoundException e) { 
			System.err.println("Conexão NÃO efetuada com o postgres -- Driver não encontrado -- " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("Conexão NÃO efetuada com o postgres -- " + e.getMessage());
		}

		return status;
	}
	
	public boolean close() {
		boolean status = false;
		
		try {
			conexao.close();
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return status;
	}
	
	
	/*INICIO DENUNCIA*/
	
	
	//INICIO --------------- POST INSERIR
	public String inserirDenuncia(Request request, Response response) {
		
		int id = 1;
		String acusado = request.queryParams("acusado");
		int quantidade = 1;
		int id_usuario = 1;
		//int quantidade = Integer.parseInt(request.queryParams("quantidade"));
		//int id_usuario = Integer.parseInt(request.queryParams("id_usuario"));

		

		Denuncia denuncia = new Denuncia(id, acusado, quantidade, id_usuario);

		inserirDenuncia(denuncia);

		response.status(201); // 201 Created
		return "Inserido com Sucesso! Acusado " + acusado;
	}
	
	public boolean inserirDenuncia(Denuncia denuncia) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			st.executeUpdate("INSERT INTO denuncias (id, acusado, quantidade, id_usuario) "
					       + "VALUES ("+denuncia.getId()+ ", '" + denuncia.getAcusado() + "', '"  
					       + denuncia.getQuantidade() + "', '" + denuncia.getId_usuario() + "');");
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
	//FIM --------------- POST INSERIR
	
	
	
	//INICIO --------------- NEXT ID	
	public int nextIdAvaliacao() {
		int tmp = -1;
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rs = st.executeQuery("SELECT id_avaliacao FROM \"Avaliacao\" ORDER BY id_avaliacao DESC LIMIT 1;");	
			
			if(rs.next()){
				tmp = rs.getInt("id_avaliacao");
	          }
			st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return tmp+1;
	}
	
	public int nextIdUsuario() {
		int tmp = -1;
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rs = st.executeQuery("SELECT id_usuario FROM \"Usuario\" ORDER BY id_usuario DESC LIMIT 1;");	
			if(rs.next()){
				tmp = rs.getInt("id_usuario");
	          }
	          st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return tmp+1;
	}
	//FIM --------------- NEXT ID
	
	
	//INICIO --------------- GET ALl cidades
			public String buscarCidadesDoEstado(Request request, Response response) throws JsonProcessingException {
			    response.header("Access-Control-Allow-Origin", "*");
			    response.header("Content-Type", "application/json");
				response.status(200); 
				String array[] = null;
				try {
					Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
					
					ResultSet rs = st.executeQuery("SELECT cidade\r\n" + 
							"FROM \"Candidatura\" \r\n" + 
							"WHERE uf = '"+request.queryParams("estado")+"'\r\n" + 
							"GROUP BY cidade");	
					System.out.println(request.queryParams("estado"));
			         if(rs.next()){
			        	 System.out.println("entrei");
			             rs.last();
			              array = new String[rs.getRow()];
			             rs.beforeFirst();

			             for(int i = 0; rs.next(); i++) {
			            	 array[i] = rs.getString("cidade");
			            	 System.out.println(array[i]);
			             }
			             
			          }
			          st.close();
				} catch (Exception e) {System.err.println(e.getMessage());}
				ObjectMapper mapper = new ObjectMapper();
				String jsonString = mapper.writeValueAsString(array);
				return jsonString;
			}
			
			//FIM --------------- GET ALL CIDADES
			
			
			
	
			
			
	//INICIO --------------- GET ALL	 POR CIDADE
		public Object buscarCandidadosPorCidade(Request request, Response response) throws JsonProcessingException {
			

		    response.header("Access-Control-Allow-Origin", "*");
		    response.header("Content-Type", "application/json");
		    
			response.status(200); 
			
			Candidatura[] candidatura = null;
			
			try {
				Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
				
				ResultSet rs = st.executeQuery("\r\n" + 
						"select * from\r\n" + 
						"\r\n" + 
						"((SELECT A.\"candidatura_id\", count(comentario) AS qtdcmts\r\n" + 
						"FROM \"Avaliacao\" A\r\n" + 
						"WHERE A.comentario IS NOT NULL\r\n" + 
						"GROUP BY A.\"candidatura_id\")) qtdavaliacao\r\n" + 
						"\r\n" + 
						"RIGHT JOIN (select * from\r\n" + 
						"(select * from\r\n" + 
						"\r\n" + 
						"(select * from\r\n" + 
						"\r\n" + 
						"(SELECT DLK.id AS codigo, DLK dlk, LK lk\r\n" + 
						"\r\n" + 
						"FROM \r\n" + 
						"(SELECT *\r\n" + 
						"FROM \r\n" + 
						" \r\n" + 
						"(SELECT A.\"candidatura_id\", count(gostei) AS lk\r\n" + 
						"FROM \"Avaliacao\" A\r\n" + 
						"WHERE A.gostei = true\r\n" + 
						"GROUP BY A.\"candidatura_id\") A\r\n" + 
						"RIGHT JOIN \"Candidatura\" C ON A.\"candidatura_id\" = C.id) LK\r\n" + 
						" \r\n" + 
						"INNER JOIN \r\n" + 
						"(SELECT * \r\n" + 
						"FROM \r\n" + 
						"(SELECT A.\"candidatura_id\", count(nao_gostei) AS dlk\r\n" + 
						"FROM \"Avaliacao\" A\r\n" + 
						"WHERE A.nao_gostei = true\r\n" + 
						"GROUP BY A.\"candidatura_id\") A\r\n" + 
						"RIGHT JOIN \"Candidatura\" C ON A.\"candidatura_id\" = C.id) DLK \r\n" + 
						" \r\n" + 
						"on LK.id = DLK.id\r\n" + 
						"GROUP BY DLK.id, DLK.dlk, LK.lk) \r\n" + 
						"Avaliacoes\r\n" + 
						"RIGHT JOIN \"Candidatura\" C on Avaliacoes.codigo = C.id)\r\n" + 
						"\r\n" + 
						"Candidatura\r\n" + 
						"INNER JOIN \"Candidato\" C ON Candidatura.\"Candidato_cpf\" = C.\"cpf\")\r\n" + 
						"Tudo\r\n" + 
						"\r\n" + 
						"LEFT JOIN (SELECT nao_gostei as desliked, gostei as liked, candidatura_id FROM \"Avaliacao\"\r\n" + 
						"WHERE usuario_id_usuario = '"+request.queryParams("idUsuario")+"' ) DLKED on Tudo.id = DLKED.candidatura_id) Z on Z.id = qtdavaliacao.candidatura_id\r\n" + 
						"\r\n" + 
						" WHERE cidade = '"+request.queryParams("cidade")+"' AND uf='"+request.queryParams("estado")+"'");
				
		         if(rs.next()){
		             rs.last();
		             candidatura = new Candidatura[rs.getRow()];
		             rs.beforeFirst();

		             for(int i = 0; rs.next(); i++) {
		            	 Candidato candidato = new Candidato( rs.getString("cpf"),  rs.getString("nome"),  rs.getString("email"),  rs.getString("nome_urna"),  rs.getString("nascionalidade"),  rs.getString("municipio_nascimento"),  rs.getString("idade"),  rs.getString("uf_nascimento"),  rs.getString("genero"),  rs.getString("ocupacao"),  rs.getString("grau_instrucao"));
		            	 Avaliacoes avaliacao = new Avaliacoes(rs.getString("cpf"), rs.getInt("lk"), rs.getInt("dlk"),rs.getInt("qtdcmts"), rs.getInt("id"));
		            	 System.out.println(avaliacao.toString());
		            	 candidatura[i] = new Candidatura(rs.getInt("id"), rs.getString("ano"), rs.getString("cidade"), rs.getString("uf"), rs.getString("cargo"), rs.getString("partido_composicao"), rs.getInt("dispesa_max"), rs.getBoolean("liked"), rs.getBoolean("desliked"), candidato, avaliacao);
		            	 System.out.println(candidatura[i].toString());
		             }
		             
		          }
		          st.close();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
			
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writeValueAsString(candidatura);
			return jsonString;
		}
	//FIM --------------- GET ALL POR CIDADE
	
	
	//INICIO --------------- POST FAZER LOGIN
		public Object login(Request request, Response response) throws JsonProcessingException {

		    response.header("Access-Control-Allow-Origin", "*");
		    response.header("Content-Type", "application/json");
		    
		    
			String userName = request.queryParams("usuario");
			String senha = request.queryParams("senha");
			
			Usuario usuario = new Usuario(-1, userName, senha);

			response.status(200); 

			return login(usuario);
		}
		
		
		public Object login(Usuario usuario) throws JsonProcessingException {
			Usuario tmp = usuario;
			try {  				
				Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
				ResultSet rs = st.executeQuery("SELECT * FROM \"Usuario\"\r\n" + 
						"WHERE usuario = '"+usuario.getUsuario()+"' and senha = '"+usuario.getSenha()+"'");
				st.close();
				
				if(rs.next()) {
					tmp = new Usuario(rs.getInt("id_usuario"), rs.getString("usuario"), rs.getString("senha"));
				}
				
			} catch (SQLException u) {  
				throw new RuntimeException(u);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writeValueAsString(tmp);
			return jsonString;
		}
	//FIM --------------- POST FAZER LOGIN
			
			
		
	//INICIO --------------- POST DAR LIKE
			public String tentativaAvaliacao(Request request, Response response) throws JsonProcessingException {
				//resolver unique userName;
				
				response.header("Access-Control-Allow-Origin", "*");
			    response.header("Content-Type", "application/json");
				
				int idAvaliacao = nextIdAvaliacao();
				String candidaturaId = request.queryParams("candidaturaId");
				String comentario = request.queryParams("comentario");
				String usuarioId = request.queryParams("usuarioId");
				boolean like = Boolean.parseBoolean(request.queryParams("like"));
				boolean deslike = Boolean.parseBoolean(request.queryParams("deslike"));				
				String acao = request.queryParams("acao");
				String cidade = request.queryParams("cidade");
				String estado = request.queryParams("estado");
				
				Avaliacao avaliacao = new Avaliacao(idAvaliacao, like, deslike, Integer.parseInt(candidaturaId ), Integer.parseInt(usuarioId), comentario, null);
				
				return verificarSeJaAvaliei(avaliacao, acao, cidade, estado);
				
				
				
			}
			
			

			public String buscarComentarios(Request request, Response response) throws JsonProcessingException {
				
				response.header("Access-Control-Allow-Origin", "*");
			    response.header("Content-Type", "application/json");
			    
				int idCandidatura = Integer.parseInt(request.queryParams("candidaturaId"));
				String cidade = request.queryParams("cidade");
				String estado = request.queryParams("estado");
				
				Avaliacao[] avaliacao = null;
				try {
					Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
					ResultSet rs = st.executeQuery("SELECT Distinct id_usuario, usuario, comentario FROM\r\n" + 
							"\"Usuario\" U\r\n" + 
							"INNER JOIN (SELECT usuario_id_usuario,cidade,uf,comentario FROM\r\n" + 
							"\"Avaliacao\" A\r\n" + 
							"INNER JOIN (SELECT * FROM \"Candidatura\") B ON A.candidatura_id = B.id\r\n" + 
							"WHERE candidatura_id = "+idCandidatura+" AND cidade = '"+cidade+"' AND uf = '"+estado+"' AND comentario IS NOT NULL\r\n" + 
							") T ON T.usuario_id_usuario = U.id_usuario");
					

					if(rs.next()){
			             rs.last();
			             avaliacao = new Avaliacao[rs.getRow()];
			             rs.beforeFirst();

			             for(int i = 0; rs.next(); i++) {
			            	 avaliacao[i] = new Avaliacao(0, false, false, 0, rs.getInt("id_usuario"), rs.getString("comentario"), rs.getString("usuario"));
			             }
			          }
					
			          st.close();
				} catch (Exception e) {System.err.println(e.getMessage());}
				
				ObjectMapper mapper = new ObjectMapper();
				String jsonString = mapper.writeValueAsString(avaliacao);
				return jsonString;
			}
			
			
			
			public Avaliacoes buscarAvaliacoes(Avaliacao avaliacao, String cidade, String estado) {
				Avaliacoes avaliacoes = null;
				try {
					Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
					ResultSet rs = st.executeQuery("\r\n" + 
							"select cpf, lk, dlk, qtdcmts,id from\r\n" + 
							"\r\n" + 
							"((SELECT A.\"candidatura_id\", count(comentario) AS qtdcmts\r\n" + 
							"FROM \"Avaliacao\" A\r\n" + 
							"WHERE A.comentario IS NOT NULL\r\n" + 
							"GROUP BY A.\"candidatura_id\")) qtdavaliacao\r\n" + 
							"\r\n" + 
							"RIGHT JOIN (select * from\r\n" + 
							"(select * from\r\n" + 
							"\r\n" + 
							"(select * from\r\n" + 
							"\r\n" + 
							"(SELECT DLK.id AS codigo, DLK dlk, LK lk\r\n" + 
							"\r\n" + 
							"FROM \r\n" + 
							"(SELECT *\r\n" + 
							"FROM \r\n" + 
							" \r\n" + 
							"(SELECT A.\"candidatura_id\", count(gostei) AS lk\r\n" + 
							"FROM \"Avaliacao\" A\r\n" + 
							"WHERE A.gostei = true\r\n" + 
							"GROUP BY A.\"candidatura_id\") A\r\n" + 
							"RIGHT JOIN \"Candidatura\" C ON A.\"candidatura_id\" = C.id) LK\r\n" + 
							" \r\n" + 
							"INNER JOIN \r\n" + 
							"(SELECT * \r\n" + 
							"FROM \r\n" + 
							"(SELECT A.\"candidatura_id\", count(nao_gostei) AS dlk\r\n" + 
							"FROM \"Avaliacao\" A\r\n" + 
							"WHERE A.nao_gostei = true\r\n" + 
							"GROUP BY A.\"candidatura_id\") A\r\n" + 
							"RIGHT JOIN \"Candidatura\" C ON A.\"candidatura_id\" = C.id) DLK \r\n" + 
							" \r\n" + 
							"on LK.id = DLK.id\r\n" + 
							"GROUP BY DLK.id, DLK.dlk, LK.lk) \r\n" + 
							"Avaliacoes\r\n" + 
							"RIGHT JOIN \"Candidatura\" C on Avaliacoes.codigo = C.id)\r\n" + 
							"\r\n" + 
							"Candidatura\r\n" + 
							"INNER JOIN \"Candidato\" C ON Candidatura.\"Candidato_cpf\" = C.\"cpf\")\r\n" + 
							"Tudo\r\n" + 
							"\r\n" + 
							"LEFT JOIN (SELECT nao_gostei as desliked, gostei as liked, candidatura_id FROM \"Avaliacao\"\r\n" + 
							") DLKED on Tudo.id = DLKED.candidatura_id) Z on Z.id = qtdavaliacao.candidatura_id\r\n" + 
							"\r\n" + 
							" WHERE cidade = '"+cidade+"' AND uf='"+estado+"'  AND id = "+avaliacao.getCandidaturaId()+"");
					
					
			         

		             if(rs.next()){
			             rs.last();
			             rs.beforeFirst();
			             for(int i = 0; rs.next(); i++) {
			            	 avaliacoes = new Avaliacoes(rs.getString("cpf"), rs.getInt("lk"), rs.getInt("dlk"),rs.getInt("qtdcmts"), rs.getInt("id"));
			             }
			          }
			          st.close();
				} catch (Exception e) {System.err.println(e.getMessage());}
				return avaliacoes;
			}
			
			
			public String atualizarVoto(Avaliacao avaliacao, String acao, String cidade, String estado) throws JsonProcessingException {
				boolean status = false;
				try {  
					Statement st = conexao.createStatement();
					st.executeUpdate("UPDATE \"Avaliacao\"\r\n" + 
							"SET nao_gostei = "+avaliacao.isNaoGostei()+", gostei= "+avaliacao.isGostei()+", comentario= '"+avaliacao.getComentario() + "'\r\n" + 
							"WHERE usuario_id_usuario = '"+avaliacao.getUsuarioId()+"' AND candidatura_id = '"+avaliacao.getCandidaturaId()+"'");
					st.close();
					status = true;
				} catch (SQLException u) {  
					throw new RuntimeException(u);
				}
				
				if(acao.equals("comentario")) {
					ObjectMapper mapper = new ObjectMapper();
					String jsonString = mapper.writeValueAsString(avaliacao);
					return jsonString;
				}else {
					
					ObjectMapper mapper = new ObjectMapper();
					String jsonString = mapper.writeValueAsString(buscarAvaliacoes(avaliacao, cidade, estado));
					return jsonString;
				}
				
			}
			
			
			
			public String avaliar(Avaliacao avaliacao, String acao, String cidade, String estado) throws JsonProcessingException {
				boolean status = false;
				try {  
					Statement st = conexao.createStatement();
					st.executeUpdate("insert into \"Avaliacao\" (id_avaliacao, gostei, nao_gostei, candidatura_id, usuario_id_usuario) VALUES ("+
					avaliacao.getId()+","
							+ avaliacao.isGostei() +","
									+ avaliacao.isNaoGostei()+","
											+ avaliacao.getCandidaturaId()+","
													+ avaliacao.getUsuarioId()+")");
					System.out.println(st);
					st.close();
					status = true;
				} catch (SQLException u) {  
					throw new RuntimeException(u);
				}
				
				
				if(acao.equals("comentario")) {
					ObjectMapper mapper = new ObjectMapper();
					String jsonString = mapper.writeValueAsString(avaliacao);
					return jsonString;
				}else {
					
					ObjectMapper mapper = new ObjectMapper();
					String jsonString = mapper.writeValueAsString(buscarAvaliacoes(avaliacao, cidade, estado));
					return jsonString;
				}
				
				
				
			}
			
			
			public String verificarSeJaAvaliei(Avaliacao avaliacao, String acao , String cidade, String estado) throws JsonProcessingException {
				try {  
					
					Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
					ResultSet rs = st.executeQuery("SELECT * FROM \"Avaliacao\"\r\n" + 
							"WHERE usuario_id_usuario = '"+avaliacao.getUsuarioId()+"' AND candidatura_id = '"+avaliacao.getCandidaturaId()+"'");		
			        
					if(rs.next()){
						avaliacao.setId(rs.getInt("id_avaliacao"));
						if(acao.equals("comentario")) {
							avaliacao.setGostei(rs.getBoolean("gostei"));
							avaliacao.setNaoGostei(rs.getBoolean("nao_gostei"));
						}else {
							avaliacao.setComentario(rs.getString("comentario"));
						}
						st.close();
						
						return atualizarVoto(avaliacao, acao, cidade, estado);
						
					}else {
						
						
						return avaliar(avaliacao, acao, cidade, estado);
					}
					
				} catch (SQLException u) {  
					throw new RuntimeException(u);
				}
			}
			
			
		//FIM --------------- POST DAR LIKE
			
			
	
	
	
	//INICIO --------------- POST INSERIR USUARIO
		public String adicionarUsuario(Request request, Response response) throws JsonProcessingException {
			//resolver unique userName;
			
			response.header("Access-Control-Allow-Origin", "*");
		    response.header("Content-Type", "application/json");
			
			int idUsuario = nextIdUsuario();
			String userName = request.queryParams("usuario");
			String senha = request.queryParams("senha");

			

			Usuario usuario = new Usuario(idUsuario, userName, senha);


			if(adicionarUsuario(usuario)) {
				response.status(200);
			}else {
				response.status(400);
				usuario.setIdUsuario(-1);				
			}

			
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writeValueAsString(usuario);
			
			
			return jsonString;
		}
		
		
		public boolean adicionarUsuario(Usuario usuario) {
			boolean status = false;
			try {  
				Statement st = conexao.createStatement();
				st.executeUpdate("INSERT INTO \"Usuario\" (id_usuario, usuario, senha) "
						       + "VALUES (" + usuario.getIdUsuario()+ ", '" + usuario.getUsuario() + "', '"  
						       + usuario.getSenha() + "');");
				st.close();
				status = true;
			} catch (SQLException u) {  
				throw new RuntimeException(u);
			}
			return status;
		}
	//FIM --------------- POST INSERIR USUARIO
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//INICIO --------------- GET ALL	
	/*public Object getCandidaturas(Request request, Response response) {
		StringBuffer returnValue = new StringBuffer("<candidaturas type=\"array\">");
		for (Candidatura candidatura : getCandidaturas()) {
			returnValue.append("\n<candidatura>\n" + 
            		"\t<id> " + candidatura.getId() + "</id>\n" +
            		"</candidatura>\n");
		}
		returnValue.append("</candidaturas>");
	    response.header("Content-Type", "application/xml");
	    response.header("Content-Encoding", "UTF-8");
		return returnValue.toString();
	}
	
	
	public Candidatura[] getCandidaturas() {
		Candidatura[] candidatura = null;
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rs = st.executeQuery("SELECT * FROM \"Candidatura\"");		
	         if(rs.next()){
	             rs.last();
	             candidatura = new Candidatura[rs.getRow()];
	             rs.beforeFirst();

	             for(int i = 0; rs.next(); i++) {
	            	 candidatura[i] = new Candidatura(rs.getInt("id"), rs.getString("ano"), rs.getString("cidade"), rs.getString("uf"), rs.getString("cargo"), rs.getString("partido_composicao"), rs.getInt("dispesa_max"), rs.getString("Candidato_cpf"));
	             }
	             
	          }
	          st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return candidatura;
	}*/
	//FIM --------------- GET ALL	
	
	
	
	
	
	
	public boolean atualizarDenuncia(Denuncia denuncia) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			String sql = "UPDATE denuncias SET acusado = '" + denuncia.getAcusado()+ "', quantidade = '"  
				       + denuncia.getQuantidade() 
					   + " WHERE id = " + denuncia.getId();
			st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
	
	public boolean excluirDenuncia(int id) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			st.executeUpdate("DELETE FROM denuncias WHERE id = " + id);
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
	
	/*FIM DENUNCIA*/
	
	
	
	
	public boolean inserirUsuario(Usuario usuario) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			st.executeUpdate("INSERT INTO usuario (codigo, login, senha, sexo) "
					       + "VALUES ("+usuario.getIdUsuario()+ ", '" + usuario.getUsuario() + "', '"  
					       + usuario.getSenha() + "');");
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
	
	public boolean atualizarUsuario(Usuario usuario) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			String sql = "UPDATE usuario SET login = '" + usuario.getUsuario() + "', senha = '"  
				       + usuario.getSenha()
					   + " WHERE codigo = " + usuario.getIdUsuario();
			st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
	
	public boolean excluirUsuario(int codigo) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			st.executeUpdate("DELETE FROM usuario WHERE codigo = " + codigo);
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
	
	
	public Usuario[] getUsuarios() {
		Usuario[] usuarios = null;
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM usuario");		
	         if(rs.next()){
	             rs.last();
	             usuarios = new Usuario[rs.getRow()];
	             rs.beforeFirst();

	             for(int i = 0; rs.next(); i++) {
	                usuarios[i] = new Usuario(rs.getInt("codigo"), rs.getString("login"), 
	                		                  rs.getString("senha"));
	             }
	          }
	          st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return usuarios;
	}

	
	public Usuario[] getUsuariosMasculinos() {
		Usuario[] usuarios = null;
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM usuario WHERE usuario.sexo LIKE 'M'");		
	         if(rs.next()){
	             rs.last();
	             usuarios = new Usuario[rs.getRow()];
	             rs.beforeFirst();

	             for(int i = 0; rs.next(); i++) {
		                usuarios[i] = new Usuario(rs.getInt("codigo"), rs.getString("login"), 
                         		                  rs.getString("senha"));
	             }
	          }
	          st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return usuarios;
	}
}