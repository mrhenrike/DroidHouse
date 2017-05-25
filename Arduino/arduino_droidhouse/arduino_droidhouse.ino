/**
 * =========================================================================
 *
 * App: Droid House
 * Versão: 0.5
 * Data: 18 de maio de 2017
 * Developers: André Henrique; Ricardo Cezar
 *
 * ======================================================================*/

/*
Explicação da Função strcmp()
Sintaxe: strcmp(palavra1,palavra2);

Onde palavra1 e palavra2 são variáveis do tipo string que devem ser comparadas. A função strcmp() pode retornar um valor nulo (zero), positivo ou negativo.
Quando as palavras comparadas são iguais, a função retorna 0. Quando as palavras comparadas são diferentes e a primeira é maior, a função retorna um valor positivo,
caso contrário, a função retorna negativo, sendo que no alfabeto a “menor” letra é “a”, e a maior, “z”.

Exemplo:
strcmp(maior,menor) == 1                   strcmp(“bb”,”aa”) ==  1
strcmp(menor,maior) == -1                  strcmp(“aa”,”bb”) == -1
strcmp(grande,grande) == 0	           strcmp(“bb”,”bb”) ==  0
strcmp(pequeno,pequeno) == 0	           strcmp(“aa”,”aa”) ==  0

--- Trabalhando com multi-tarefa ---

Todos os leds aguardando em real time os comandos para ligar/desligar, que vierem do Android. Paralelamente a isso, uma funcao envia dados do sensor de temperatura a cada 30 segundos.

*/

#include <Thread.h>
#include <ThreadController.h>

 /* Declara uma thread "raiz" um controle que agrupa as thread's filhas.. */
 ThreadController cpu;
 Thread threadRecebeDadosandroid;
 
 /* Pino que conecta os pinos do LED */
 const int LED1 = 9;
 const int LED2 = 10;
 const int LED3 = 11;
 const int sinalparaorele = 7; 

/* Declaração de variáveis para controle e separação das strings recebidas do Android */
 boolean separador = false;
 const uint8_t BUFFER = 20;
 char dispositivo[BUFFER];
 uint8_t tamDispositivo = 0;
 char valor[5];
 uint8_t tamValor = 0;
 int aux = 0;

/* Char utilizado para ler o caracteres recebidos do Android */
char serialByte = 0;

/* Declaração do Pin Analalógico em que está conectado o Sensor de Temperatura */
const int tempSensor = A0;

/* Variação dos valores do sensor */
float sensorValue = 0;

/* Variável para controle das tarefas simultâneas */
unsigned long start = 0;

void setup() {
  // Serial communication at 9600 bps
  Serial.begin(9600);
  
  /* Define o pino digital como saída. */
  pinMode(LED1, OUTPUT);
  pinMode(LED2, OUTPUT);
  pinMode(LED3, OUTPUT);
  pinMode(sinalparaorele, OUTPUT);

  threadRecebeDadosandroid.setInterval(20);
  threadRecebeDadosandroid.onRun(recebeDadosAndroid);
  
  /* Adiciona as thread's filhas a thread raiz ou mãe.. */
  cpu.add(&threadRecebeDadosandroid);
}

void loop() {
   
   /* Start a thread raiz.. */
  cpu.run();
  
  unsigned long current = millis();
  if ((current - start) >= 30000) {
   
  readSensorTemp();
  sendDataAndroid();
  start = current;
  
  }
  
}

/**
 * recebeDadosAndroid -
 */  
 void recebeDadosAndroid() {
  if(Serial.available()) { /* Enquanto houverem bytes disponíveis; */
    char c = Serial.read(); /* Lê byte do buffer serial; */
    
    if (c == '\n') {
      if (tamDispositivo == 0) return;
      dispositivo[tamDispositivo] = 0;
      tamDispositivo = 0;

      valor[tamValor] = 0;
      tamValor = 0;

      aux = atoi(valor);
      separador = false; 
      
      if (!strcmp(dispositivo, "led_on_all") && !strcmp(valor, "1")){ led_on_all();}
      else if (!strcmp(dispositivo, "led_off_all") && !strcmp(valor, "0")){ led_off_all();}
      
      if (!strcmp(dispositivo, "led_on_1") && !strcmp(valor, "1")){ led_on_1();}
      else if (!strcmp(dispositivo, "led_off_1") && !strcmp(valor, "0")){ led_off_1();}
      
      if (!strcmp(dispositivo, "led_on_2") && !strcmp(valor, "1")){ led_on_2();}
      else if (!strcmp(dispositivo, "led_off_2") && !strcmp(valor, "0")){ led_off_2();}
      
      if (!strcmp(dispositivo, "led_on_3") && !strcmp(valor, "1")){ led_on_3();}
      else if (!strcmp(dispositivo, "led_off_3") && !strcmp(valor, "0")){ led_off_3();}
    }
    
    else if (tamDispositivo < BUFFER - 1) { // Verifica, separa e incrementa os dados.
      if(c != '+' && separador == false){
        dispositivo[tamDispositivo] = c;
        tamDispositivo++;        
      }
      else if(c == '+'){
        separador = true;
      } 
      else {
        valor[tamValor] = c;
        tamValor++;
      }
    }
  }

}


/**
 * led_on -
 */
 void led_on_all() {
  digitalWrite(LED1, HIGH); digitalWrite(LED2, HIGH); digitalWrite(LED3, HIGH);
}

/**
 * led_off -
 */
 void led_off_all() {
  digitalWrite(LED1, LOW); digitalWrite(LED2, LOW); digitalWrite(LED3, LOW);
}

void led_on_1() {
  digitalWrite(LED1, HIGH);
}

void led_off_1() {
  digitalWrite(LED1, LOW);
}

void led_on_2() {
  digitalWrite(LED2, HIGH);
}

void led_off_2() {
  digitalWrite(LED2, LOW);
}

void led_on_3() {
  //digitalWrite(LED3, HIGH);
  digitalWrite(sinalparaorele, HIGH); //Aciona o rele
}

void led_off_3() {
  //digitalWrite(LED3, LOW);
  digitalWrite(sinalparaorele, LOW); //Desliga o rele
}

void readSensorTemp() {
  sensorValue = (5.0 * analogRead(tempSensor) * 100.0) / 1024.0;
  
  // It will send data every two seconds
  //delay(20);
}

void sendDataAndroid() {
  Serial.print(sensorValue);
  Serial.print('\n');
  //delay(10);
}
