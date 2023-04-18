package com.projetoSpringApiRestfull.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.projetoSpringApiRestfull.model.UserChart;
import com.projetoSpringApiRestfull.model.UserReport;
import com.projetoSpringApiRestfull.model.Usuario;
import com.projetoSpringApiRestfull.repository.TelefoneRepository;
import com.projetoSpringApiRestfull.repository.UsuarioRepository;
import com.projetoSprirngApiRestfull.service.ImplementsUserDetailsService;
import com.projetoSprirngApiRestfull.service.ServiceRelatorio;


@CrossOrigin(origins = "*") // liberando requisição de qualquer servidor para o controller
@RestController
@RequestMapping(value = "/usuario")
@CacheEvict(cacheNames = "cacheusersall", allEntries = true)
@CachePut(cacheNames = "cacheusersall")  //traz mudanças novas e atualiza o cache
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ImplementsUserDetailsService implementsUserDetailsService;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ServiceRelatorio serviceRelatorio;
	
	@Autowired
	private JdbcTemplate jdbcTemplate; //para usar sql puro no endpoint de grafico
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {	
		
			Optional<Usuario> usuario = usuarioRepository.findById(id);
			
			return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
		}
		
	@GetMapping(value = "/" , produces = "application/json")
	public ResponseEntity<Page<Usuario>> usuariosAll() {
			
			PageRequest page = PageRequest.of(0, 3, Sort.by("nome"));
			Page<Usuario> list = usuarioRepository.findAll(page);
			
			return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
		}
	
	@GetMapping(value = "/pagination/{page}" , produces = "application/json")
	public ResponseEntity<Page<Usuario>> usersPagination(@PathVariable("page") int pagina) {
			
			PageRequest page = PageRequest.of(pagina, 3, Sort.by("nome"));
			Page<Usuario> list = usuarioRepository.findAll(page);
			
			return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
		}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrarUser(@RequestBody Usuario usuario) throws Exception{
		
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos ++) {
				
				usuario.getTelefones().get(pos).setUsuario(usuario); //busca os telefones e seta no id dos telefones o usuario
			 }
			
		if(usuario.getCep() != null && !usuario.getCep().isEmpty()) {

			
			//Consumindo API externa via cep
			URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
			URLConnection connection = url.openConnection(); //pedido de conexão
			InputStream inputStream = connection.getInputStream(); //vem os dados da requisição
			BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); //prepara leitura
			
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			
			while((cep = bf.readLine()) != null) {
				
				jsonCep.append(cep);
				
			}
			
			Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class); //transformando o json em objeto com base no usuario
			
			usuario.setCep(userAux.getCep());
			usuario.setLogradouro(userAux.getLogradouro());
			usuario.setComplemento(userAux.getComplemento());
			usuario.setBairro(userAux.getBairro());
			usuario.setLocalidade(userAux.getLocalidade());
			usuario.setUf(userAux.getUf());
			
		}
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuariosalvo = usuarioRepository.save(usuario);
		implementsUserDetailsService.insereAcessoPadrao(usuariosalvo.getId()); //insere role 
		
		return new ResponseEntity<Usuario>(usuariosalvo, HttpStatus.OK);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> updateUser (@RequestBody Usuario usuario){
		
			for(int pos = 0; pos < usuario.getTelefones().size(); pos ++) {
				usuario.getTelefones().get(pos).setUsuario(usuario); 
			
			}
			Usuario userTemp = usuarioRepository.findById(usuario.getId()).get();
			
			if (!userTemp.getSenha().equals(usuario.getSenha())) {
	
				String senhaAttCripto = new BCryptPasswordEncoder().encode(usuario.getSenha());
				usuario.setSenha(senhaAttCripto);
			}
			
			Usuario usuarioatt = usuarioRepository.save(usuario);
			return new ResponseEntity<Usuario>(usuarioatt, HttpStatus.OK);	
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String deleteFromUser(@PathVariable("id") Long id)	{
		
			usuarioRepository.deleteById(id);
			return "Usuario deletado com sucesso";
		}
	
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	public ResponseEntity<Page<Usuario>> userByName(@PathVariable("nome") String nome){ //busca user por nome like
		
		Pageable pageRequest = null;
		Page<Usuario> list = null;
		
		if(nome == null || (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")) { // nao informa nome
				
			pageRequest = PageRequest.of(0, 3, Sort.by("nome"));
			list = usuarioRepository.findAll(pageRequest);

		}else { // informa nome
			
			pageRequest = PageRequest.of(0, 4, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	@GetMapping(value = "/usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	public ResponseEntity<Page<Usuario>> userByNamePage(@PathVariable("nome") String nome, 
				@PathVariable("page") int page){
		
		Pageable pageable = null;
		Page<Usuario> list = null;
		
		if(nome == null || (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")) { // nao informa nome
				
			pageable = PageRequest.of(page, 3, Sort.by("nome"));
			list = usuarioRepository.findAll(pageable);

		}else { // informa nome
			
			pageable = PageRequest.of(page, 3, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageable);
		}
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "removeTelephone/{id}", produces = "application/text")
	public String deleteTelephone(@PathVariable("id") Long id) {
		
		
		telefoneRepository.deleteById(id);
		return "com sucesso";
	}
	
	@GetMapping(value = "/relatorio", produces = "application/text")
	public ResponseEntity<String> downloadRelatorio(HttpServletRequest request) throws Exception{
		
		byte[] pdf = serviceRelatorio.gerarRelatorio("RelUser", new HashMap<>(),request.getServletContext()); 
		
		String base64pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf); //byte para a string base64
		
		return new ResponseEntity<String>(base64pdf, HttpStatus.OK);
	}
	
	@PostMapping(value = "relatorio/", produces = "application/text")
	public ResponseEntity<String> downloadRelatorioParam(HttpServletRequest request,
				@RequestBody UserReport userReport) throws Exception{
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat dateFormatParam = new SimpleDateFormat("yyyy-MM-dd");
		
		String dataInicio = dateFormatParam.format(dateFormat.parse(userReport.getDataInicio())); // formatando data para banco
		String dataFim = dateFormatParam.format(dateFormat.parse(userReport.getDataFim()));
		
		Map<String , Object> params = new HashMap<String, Object>();
		
		params.put("DATA_INICIO", dataInicio);
		params.put("DATA_FIM", dataFim);
		
		byte[] pdf = serviceRelatorio.gerarRelatorio("RelUser-param", params ,request.getServletContext()); 
		
		String base64pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf); //byte para a string base64
		
		return new ResponseEntity<String>(base64pdf, HttpStatus.OK);
	}
	
	@GetMapping(value = "/grafico" , produces = "application/json")
	 public ResponseEntity<UserChart> graficoInChartUserSalario() {
		 
		UserChart chart = new UserChart();
		
		List<String> resultadoQueryChart = jdbcTemplate.queryForList("SELECT array_agg(nome)\r\n"
				+ "	FROM usuario \r\n"
				+ "	WHERE salario > 0 and nome <> ''\r\n"
				+ "	\r\n"
				+ "	UNION ALL\r\n"
				+ "	\r\n"
				+ "	SELECT CAST(array_agg(salario) as character VARYING[]) \r\n"
				+ "		FROM usuario \r\n"
				+ "			WHERE salario > 0 \r\n"
				+ "			and nome <> '' \r\n"
				+ "			and salario is not NULL", String.class);
		
		if(!resultadoQueryChart.isEmpty()) {
			
			String nomes = resultadoQueryChart.get(0).
					replaceAll("\\{", "").replaceAll("\\}", ""); // pega linha 1 do sql e remove as {}
			
			String salarios = resultadoQueryChart.get(1).
					replaceAll("\\{", "").replaceAll("\\}", ""); // pega linha 2 do sql e remove as {} 
			
			chart.setNome(nomes);
			chart.setSalario(salarios);
			
		}
		return new ResponseEntity<UserChart>(chart, HttpStatus.OK);
		
	 }
}

