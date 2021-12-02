package ManipulacaoBancoDeDados;

import ExceptionsCustomizadas.CadastroException;

import java.io.IOException;

import java.util.Set;
import java.util.TreeSet;

import ClassesUtilitarias.CharIOMaster;
import ClassesUtilitarias.Cliente;

public class ControleDeCadastroDeClientes {
	private static final String caminhoPastaBancoDeDados = "Projeto POO" + System.getProperty("file.separator") + "BancoDeDados";
	private static final String caminhoBancoDeDados = caminhoPastaBancoDeDados + System.getProperty("file.separator") + "RegistroDeClientes.txt";
	private static final String caminhoBancoDeDadosTemp = caminhoPastaBancoDeDados + System.getProperty("file.separator") + "RegistroDeClientesTemp.txt";
	private static final String separadorDeinformacoes = "/";

	public static void cadastrarCliente(Cliente novoCliente) throws IOException{
		CharIOMaster ciomaster = new CharIOMaster(caminhoBancoDeDados, 'r', separadorDeinformacoes);

		String cliente;
		String[] informacoesClienteCliente;

		try {
			while((cliente = (String)ciomaster.ler()) != null){
				informacoesClienteCliente = cliente.split(separadorDeinformacoes);
	
				if(informacoesClienteCliente[1].equals(novoCliente.getCPF())) {
					throw new CadastroException();
				}
			}

			ciomaster.fecharArquivos();

			ciomaster = new CharIOMaster(caminhoBancoDeDados, 'a', separadorDeinformacoes);

			String[] infoNovoCliente = {novoCliente.getNome(), novoCliente.getCPF(), novoCliente.getCEP(), novoCliente.getId(), String.valueOf(1)};
			ciomaster.escrever(infoNovoCliente);

		}catch(CadastroException ce){
			System.err.println("Cliente com CPF " + novoCliente.getCPF() + " já está cadastrado.");
		}finally{
			ciomaster.fecharArquivos();
		}
	}

	public static void reCadastrarCliente(Cliente novoCliente) throws IOException{
		CharIOMaster ciomaster = new CharIOMaster(caminhoBancoDeDados, caminhoBancoDeDadosTemp, separadorDeinformacoes);

		String cliente;
		String[] informacoesCliente;

		try {
			while((cliente = (String)ciomaster.ler()) != null){
				informacoesCliente = cliente.split(separadorDeinformacoes);
	
				if(informacoesCliente[1].equals(novoCliente.getCPF())) {
					if(Integer.valueOf(informacoesCliente[3]).equals(0)){
						informacoesCliente[0] = novoCliente.getNome();
						informacoesCliente[2] = novoCliente.getCEP();
						informacoesCliente[3] = novoCliente.getId();
						informacoesCliente[4] = String.valueOf(1);
					}else
						throw new CadastroException("Cliente já cadastrado.");
				}
				
				ciomaster.escrever(informacoesCliente);
			}

			ciomaster.fecharArquivos();

			CharIOMaster.renomearESobrescreverArquivo(caminhoBancoDeDados, caminhoBancoDeDadosTemp);

		}catch(CadastroException ce){
			ce.printStackTrace();
			
			ciomaster.fecharArquivos();

			CharIOMaster.deletarArquivo(caminhoBancoDeDadosTemp);
		}
	}

	public static boolean procurarCliente(String CPF) throws IOException{
		CharIOMaster ciomaster = new CharIOMaster(caminhoBancoDeDados, 'r', separadorDeinformacoes);
		
		String cliente;
		String[] informacoesCliente;

		while((cliente = (String)ciomaster.ler()) != null){
			informacoesCliente = cliente.split(separadorDeinformacoes);

			if(CPF.equals(informacoesCliente[1]))
				return Integer.valueOf(informacoesCliente[4]) == 1;
		}

		ciomaster.fecharArquivos();

		return false;
	}

	public static void excluirCliente(String CPF) throws IOException{
		CharIOMaster ciomaster = new CharIOMaster(caminhoBancoDeDados, caminhoBancoDeDadosTemp, separadorDeinformacoes);

		String cliente;
		String[] informacoesCliente;

		try {
			while((cliente = (String)ciomaster.ler()) != null){
				informacoesCliente = cliente.split(separadorDeinformacoes);
	
				if(informacoesCliente[1].equals(CPF)) {
					if(Integer.valueOf(informacoesCliente[4]).equals(0)){
						throw new CadastroException("Cliente já excluído.");
					}else
						informacoesCliente[4] = String.valueOf(0);
				}
				
				ciomaster.escrever(informacoesCliente);
			}

			ciomaster.fecharArquivos();

			CharIOMaster.renomearESobrescreverArquivo(caminhoBancoDeDados, caminhoBancoDeDadosTemp);

		}catch(CadastroException ce){
			ce.printStackTrace();
			
			ciomaster.fecharArquivos();

			CharIOMaster.deletarArquivo(caminhoBancoDeDadosTemp);
		}
	}

	private static void prototipoListarClientes(boolean val) throws IOException{
		CharIOMaster ciomaster = new CharIOMaster(caminhoBancoDeDados, 'r', separadorDeinformacoes);

		Set<String> nomeClientes = new TreeSet<String>();
		String cliente;
		String[] informacoesCliente;

		while((cliente = (String)ciomaster.ler()) != null){
			informacoesCliente = cliente.split(separadorDeinformacoes);

			// Operador XOR agindo como negação.
			// Se val == true, a expressão no if é equivalente à "!(Integer.valueOf(informacoesCliente[4]) == 1)"
			// Se val == false, a expressão no if é equivalente à "Integer.valueOf(informacoesCliente[4]) == 1"
			if((Integer.valueOf(informacoesCliente[4]) == 1) ^ val)
				nomeClientes.add(informacoesCliente[0]);
		}
		
		for(String s : nomeClientes)
			System.out.println(s);

		ciomaster.fecharArquivos();
	}

	public static void listarClientesCadastrados() throws IOException{
		prototipoListarClientes(false);
	}

	public static void listarClientesExcluidos() throws IOException{
		prototipoListarClientes(true);
	}

	public static void main(String[] args) {
		try{
			cadastrarCliente(new Cliente("Jonas", "289.875.960-01", "69027-410"));
			cadastrarCliente(new Cliente("Maria", "606.981.120-83", "74990-725"));
			cadastrarCliente(new Cliente("Kalil", "087.532.840-70", "78085-778"));
			cadastrarCliente(new Cliente("Wellington Felipe", "424.844.250-74", "49090-073"));
			cadastrarCliente(new Cliente("Matheus Miller", "425.332.960-82", "68908-351"));
			listarClientesCadastrados();
			System.out.println("-------------------------");
			listarClientesExcluidos();
		}catch(IOException e){
			System.out.println("Falha na comunicação com o banco de dados.");
		}
	}
}