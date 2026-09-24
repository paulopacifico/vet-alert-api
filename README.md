# Vet Alert API

API secundária do MVP de prontuário e farmacovigilância de animais resgatados. Recebe da API principal [`vetcare-api`](https://github.com/paulopacifico/vetcare-api) o resumo agregado dos eventos adversos obtidos na openFDA, calcula um score de risco farmacológico, classifica o nível e mantém o histórico das avaliações em PostgreSQL.

Esta API não conhece a openFDA nem o prontuário. Ela isola a regra de negócio do cálculo de risco, o que permite ajustar os pesos do score sem alterar a aplicação principal.

A arquitetura completa, com o fluxograma, está no README da `vetcare-api`.

## Regra de cálculo

```
proporcaoGrave = relatosGraves / totalRelatos
fatorVolume    = min(totalRelatos / 1000, 1)
score          = proporcaoGrave * 70 + fatorVolume * 30
```

| Condição | Nível |
|---|---|
| `totalRelatos = 0` | `SEM_DADOS` |
| score < 30 | `BAIXO` |
| 30 ≤ score ≤ 60 | `MODERADO` |
| score > 60 | `ALTO` |

A gravidade pesa mais que o volume: um fármaco com poucos relatos, mas quase todos graves, sobe de nível. Quando não existe nenhum relato, o resultado é `SEM_DADOS` e não `BAIXO`, porque ausência de dados não indica segurança. Se `relatosGraves` vier maior que `totalRelatos`, o valor é limitado ao total.

## Rotas

| Método | Rota | Descrição |
|---|---|---|
| POST | `/avaliacoes` | Calcula o score e grava a avaliação |
| GET | `/avaliacoes` | Lista avaliações, com filtro opcional por `farmaco` (parcial) e `especie` (exato) |
| GET | `/avaliacoes/{id}` | Busca uma avaliação pelo identificador |
| DELETE | `/avaliacoes/{id}` | Remove uma avaliação do histórico |

Documentação Swagger em http://localhost:8081/swagger-ui.html
Healthcheck em http://localhost:8081/actuator/health

### Exemplo

```bash
curl -X POST http://localhost:8081/avaliacoes \
  -H "Content-Type: application/json" \
  -d '{
        "farmaco": "carprofen",
        "especie": "CAO",
        "totalRelatos": 1200,
        "relatosGraves": 300,
        "reacoesTop": ["Vomiting", "Diarrhoea", "Lethargy"]
      }'
```

```json
{
  "id": 1,
  "farmaco": "carprofen",
  "especie": "CAO",
  "totalRelatos": 1200,
  "relatosGraves": 300,
  "score": 47.5,
  "nivel": "MODERADO",
  "reacoesTop": ["Vomiting", "Diarrhoea", "Lethargy"],
  "criadoEm": "2026-08-20T14:32:10.221"
}
```

## Tecnologias

- Java 21
- Spring Boot 3.3.5 (Web, Data JPA, Validation, Actuator)
- springdoc-openapi 2.6.0
- PostgreSQL 16

## Pré-requisitos

- Docker
- Para rodar sem Docker: Java 21 e Maven 3.9

## Execução com Docker

O jeito mais simples é subir tudo pelo `docker-compose.yml` da `vetcare-api`, que já inclui o PostgreSQL e constrói esta API a partir deste repositório.

Para rodar esta API isoladamente:

```bash
docker network create vetalert-net

docker run -d --name vetalert-db --network vetalert-net \
  -e POSTGRES_DB=vetalert -e POSTGRES_USER=vetalert -e POSTGRES_PASSWORD=vetalert \
  postgres:16-alpine

docker build -t vet-alert-api .

docker run --rm -p 8081:8081 --network vetalert-net \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://vetalert-db:5432/vetalert \
  vet-alert-api
```

O Dockerfile usa build em dois estágios (Maven para compilar, JRE para executar) e declara um `HEALTHCHECK` no endpoint do Actuator.

## Execução local sem Docker

Suba um PostgreSQL na porta 5432 com banco, usuário e senha `vetalert`:

```bash
docker run -d --name vetalert-db -p 5432:5432 \
  -e POSTGRES_DB=vetalert -e POSTGRES_USER=vetalert -e POSTGRES_PASSWORD=vetalert \
  postgres:16-alpine

mvn spring-boot:run
```

O esquema é criado automaticamente pelo Hibernate (`ddl-auto: update`).

## Variáveis de ambiente

| Variável | Padrão |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/vetalert` |
| `SPRING_DATASOURCE_USERNAME` | `vetalert` |
| `SPRING_DATASOURCE_PASSWORD` | `vetalert` |

## Estrutura

```
src/main/java/com/vetalert/
├── VetAlertApplication.java
├── config/
│   ├── OpenApiConfig.java
│   └── TratadorDeErros.java
├── controller/
│   └── AvaliacaoController.java
├── domain/
│   ├── Avaliacao.java
│   └── NivelRisco.java
├── dto/
│   ├── AvaliacaoRequest.java
│   └── AvaliacaoResponse.java
├── repository/
│   └── AvaliacaoRepository.java
└── service/
    └── AvaliacaoService.java
```
