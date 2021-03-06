package ManipulacaoBancoDeDados;

import ExceptionsCustomizadas.CadastroException;

import java.io.IOException;

import ClassesUtilitarias.CharIOMaster;
import RegrasDeNegocio.Cliente;

public class ControleDeCadastroDeClientes {
	private String caminhoBancoDeDados;
	private String caminhoBancoDeDadosTemp;
	private String separadorDeinformacoes = "/";

	public ControleDeCadastroDeClientes(String caminhoPastaBancoDeDados) {
		this.caminhoBancoDeDados = caminhoPastaBancoDeDados + System.getProperty("file.separator") + "RegistroDeClientes.txt";
		this.caminhoBancoDeDadosTemp = caminhoPastaBancoDeDados + System.getProperty("file.separator") + "RegistroDeClientesTemp.txt";
	}

	public void cadastrarCliente(Cliente novoCliente) throws IOException, CadastroException{
		CharIOMaster ciomaster = new CharIOMaster(caminhoBancoDeDados, caminhoBancoDeDadosTemp, separadorDeinformacoes);

		String cliente;
		String[] informacoesCliente;

		while((cliente = (String)ciomaster.ler()) != null){
			informacoesCliente = cliente.split(separadorDeinformacoes);
	
			if(informacoesCliente[1].equals(novoCliente.getCPF())) {
				if(Integer.valueOf(informacoesCliente[4]) == 1)
					throw new CadastroException();
				else{
					informacoesCliente[0] = novoCliente.getNome();
					informacoesCliente[2] = novoCliente.getCEP();
					informacoesCliente[3] = novoCliente.getId();
					informacoesCliente[4] = String.valueOf(1);
				}
			}
			ciomaster.escrever(informacoesCliente);
		}

		ciomaster.fecharArquivos();
		CharIOMaster.renomearESobrescreverArquivo(caminhoBancoDeDados, caminhoBancoDeDadosTemp);
	}

	public Cliente procurarCliente(String CPF) throws IOException, CadastroException{
		CharIOMaster ciomaster = new CharIOMaster(caminhoBancoDeDados, 'r', separadorDeinformacoes);
		
		String cliente;
		String[] informacoesCliente;

		while((cliente = (String)ciomaster.ler()) != null){
			informacoesCliente = cliente.split(separadorDeinformacoes);

			if(CPF.equals(informacoesCliente[1])) {
				if(Integer.valueOf(informacoesCliente[4]) == 1)
					return new Cliente(informacoesCliente[0], informacoesCliente[1], informacoesCliente[2]);
				else
					throw new CadastroException("Cliente excluido");
			}
				
		}
		ciomaster.fecharArquivos();
		return null;
	}

	public void excluirCliente(String CPF) throws IOException{
		CharIOMaster ciomaster = new CharIOMaster(caminhoBancoDeDados, caminhoBancoDeDadosTemp, separadorDeinformacoes);

		String cliente;
		String[] informacoesCliente;

		try {
			while((cliente = (String)ciomaster.ler()) != null){
				informacoesCliente = cliente.split(separadorDeinformacoes);
	
				if(informacoesCliente[1].equals(CPF)) {
					if(Integer.valueOf(informacoesCliente[4]).equals(0)){
						throw new CadastroException("Cliente j?? exclu??do.");
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

	private String prototipoListarClientes(boolean val) throws IOException{
		CharIOMaster ciomaster = new CharIOMaster(caminhoBancoDeDados, 'r', separadorDeinformacoes);

		String cliente;
		String[] informacoesCliente;

		StringBuilder listaClientes = new StringBuilder();

		while((cliente = (String)ciomaster.ler()) != null){
			informacoesCliente = cliente.split(separadorDeinformacoes);

			// Operador XOR agindo como nega????o.
			// Se val == true, a express??o no if ?? equivalente ?? "!(Integer.valueOf(informacoesCliente[4]) == 1)"
			// Se val == false, a express??o no if ?? equivalente ?? "Integer.valueOf(informacoesCliente[4]) == 1"
			if((Integer.valueOf(informacoesCliente[4]) == 1) ^ val)
				listaClientes.append(String.format("Nome: %-20s		CPF: %-10s 		CEP:%-10s \n", 
													informacoesCliente[0], 
													informacoesCliente[1], 
													informacoesCliente[2]));
		}


		ciomaster.fecharArquivos();

		return listaClientes.toString();
	}

	public String listarClientesCadastrados() throws IOException{
		return prototipoListarClientes(false);
	}

	public String listarClientesExcluidos() throws IOException{
		return prototipoListarClientes(true);
	}

	public static void main(String[] args) {
		try{
			ControleDeCadastroDeClientes controleDeCadastroDeClientes = new ControleDeCadastroDeClientes("src" + System.getProperty("file.separator") + "BancoDeDados");
			controleDeCadastroDeClientes.cadastrarCliente(new Cliente("Jonas", "289.875.960-01", "69027-410"));
			controleDeCadastroDeClientes.cadastrarCliente(new Cliente("Maria", "606.981.120-83", "74990-725"));
			controleDeCadastroDeClientes.cadastrarCliente(new Cliente("Kalil", "087.532.840-70", "78085-778"));
			controleDeCadastroDeClientes.cadastrarCliente(new Cliente("Wellington Felipe", "424.844.250-74", "49090-073"));
			controleDeCadastroDeClientes.cadastrarCliente(new Cliente("Matheus Miller", "425.332.960-82", "68908-351"));
			controleDeCadastroDeClientes.listarClientesCadastrados();
			System.out.println("-------------------------");
			controleDeCadastroDeClientes.listarClientesExcluidos();
		}catch(IOException e){
			System.out.println("Falha na comunica????o com o banco de dados.");
		}catch(CadastroException ce) {
			ce.printStackTrace();
		}
	}
}
