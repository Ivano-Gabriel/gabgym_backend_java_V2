package com.gabgym.api.config;

import com.gabgym.api.entities.Exercise;
import com.gabgym.api.entities.FoodCategory;
import com.gabgym.api.entities.FoodItem;
import com.gabgym.api.entities.MuscleGroup;
import com.gabgym.api.entities.User;
import com.gabgym.api.repositories.ExerciseRepository;
import com.gabgym.api.repositories.FoodCategoryRepository;
import com.gabgym.api.repositories.FoodItemRepository;
import com.gabgym.api.repositories.MuscleGroupRepository;
import com.gabgym.api.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(MuscleGroupRepository muscleGroupRepo,
                                       ExerciseRepository exerciseRepo,
                                       FoodCategoryRepository categoryRepo,
                                       FoodItemRepository foodItemRepo,
                                       UserRepository userRepo,
                                       PasswordEncoder passwordEncoder) {
        return args -> {

            // ===================================================================
            // 0. USUÁRIO DE TESTE (pra você logar direto sem recadastrar toda hora)
            //    login: teste   senha: teste123
            // ===================================================================
            if (userRepo.findByUsername("teste").isEmpty()) {
                System.out.println(">>> Criando usuário de teste...");
                User dummy = new User();
                dummy.setUsername("teste");
                dummy.setPassword(passwordEncoder.encode("teste123"));
                dummy.setName("Gabriel Teste");
                dummy.setAge(28);
                dummy.setWeight(80.0);
                dummy.setHeight(178.0);
                dummy.setGender("male");
                dummy.setObjective("gain-muscle");
                dummy.setActivityLevel("moderate");
                userRepo.save(dummy);
            }

            // ===================================================================
            // 1. ALIMENTOS (roda só se a tabela de categorias estiver vazia)
            // ===================================================================
            if (categoryRepo.count() == 0) {
                System.out.println(">>> Populando ALIMENTOS...");

                Map<String, FoodCategory> categorias = new HashMap<>();
                for (String nome : List.of("Proteínas", "Carboidratos", "Frutas & Legumes",
                        "Gorduras Boas", "Bebidas", "Doces & Sobremesas", "Suplementos", "Dia do Lixo")) {
                    FoodCategory cat = new FoodCategory();
                    cat.setName(nome);
                    categorias.put(nome, categoryRepo.save(cat));
                }

                // --- PROTEÍNAS ---
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Ovo Cozido", "1 unidade", "1 ovo grande", 78, 6, 1, 5, "/imagens_comidas/ovo.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Ovo Frito", "1 unidade", "1 ovo grande", 90, 6, 1, 7, "/imagens_comidas/ovo_frito.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Frango Grelhado", "100g", "1 filé pequeno", 165, 31, 0, 3.6, "/imagens_comidas/frango.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Frango Cozido Desfiado", "100g", "Aprox. 1 xícara", 170, 32, 0, 4, "/imagens_comidas/frango_cozido.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Bife Grelhado", "100g", "1 bife do tamanho da palma da mão", 250, 26, 0, 15, "/imagens_comidas/bife.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Carne Cozida", "100g", "Aprox. 4 colheres de sopa", 215, 30, 0, 10, "/imagens_comidas/carne_cozida.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Carne Moída", "100g", "Aprox. 4 colheres de sopa", 220, 28, 0, 12, "/imagens_comidas/carne_moida.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Peixe Cozido (Tilápia)", "100g", "1 filé médio", 130, 26, 0, 2.5, "/imagens_comidas/peixe_cozido.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Peixe Frito", "100g", "1 filé médio", 200, 20, 8, 10, "/imagens_comidas/peixe_frito.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Sardinha em Lata (em óleo)", "85g", "1 lata pequena drenada", 180, 21, 0, 10, "/imagens_comidas/sardinha.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Queijo Coalho", "1 fatia média", "Aprox. 30g", 100, 7, 1, 8, "/imagens_comidas/queijo_coalho.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Salsicha Cozida", "1 unidade", "Aprox. 50g", 150, 5, 1, 14, "/imagens_comidas/salsicha.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Calabresa Frita", "50g", "Aprox. 1/2 gomo pequeno", 200, 10, 2, 17, "/imagens_comidas/calabresa.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Mortadela", "2 fatias", "Aprox. 40g", 160, 8, 2, 14, "/imagens_comidas/kitut.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Presunto Cozido", "2 fatias", "Aprox. 40g", 60, 7, 1, 3, "/imagens_comidas/presunto.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Bisteca Suína", "100g", "1 bisteca média", 230, 25, 0, 14, "/imagens_comidas/bisteca.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Fígado de Boi Grelhado", "100g", "1 bife médio", 140, 20, 4, 4, "/imagens_comidas/figado.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Queijo Minas Frescal", "30g", "1 fatia grossa", 70, 5, 1, 5, "/imagens_comidas/queijo_minas.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Proteínas"), "Iogurte Grego Natural", "100g", "1 pote pequeno", 95, 10, 4, 4.5, "/imagens_comidas/iogurte_grego.jpg");

                // --- CARBOIDRATOS ---
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Cuscuz Nordestino", "100g", "Aprox. 1 pires cheio", 112, 3.5, 25, 0, "/imagens_comidas/cuscuz.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Batata Doce Cozida", "100g", "Aprox. 1 unidade pequena", 86, 1.6, 20, 0.1, "/imagens_comidas/batata-doce.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Macaxeira (Aipim)", "100g", "Aprox. 3 pedaços médios", 160, 1.4, 38, 0.3, "/imagens_comidas/macaxeira.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Arroz Branco Cozido", "100g", "Aprox. 4 colheres de sopa cheias", 130, 2.7, 28, 0.3, "/imagens_comidas/arroz.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Feijão Carioca Cozido", "100g", "Aprox. 1 concha média", 76, 5, 14, 0.5, "/imagens_comidas/feijao.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Macarrão Cozido", "100g", "Aprox. 1 xícara", 158, 5, 31, 1, "/imagens_comidas/macarrao.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Purê de Batata", "100g", "Aprox. 3 colheres de sopa", 88, 2, 21, 0, "/imagens_comidas/pure_batata.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Aveia em Flocos", "40g", "Aprox. 4 colheres de sopa", 150, 6, 27, 3, "/imagens_comidas/aveia.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Pão Francês", "1 unidade", "1 pão de 50g", 140, 4, 29, 1.5, "/imagens_comidas/pao_frances.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Bolacha Cream Cracker", "3 unidades", "Porção individual", 100, 2, 15, 3, "/imagens_comidas/cream_cracker.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Pão de Queijo", "1 unidade", "1 unidade média de padaria", 170, 5, 20, 7, "/imagens_comidas/pao_de_queijo.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Tapioca (só a goma)", "100g", "1 tapioca grande", 240, 0, 60, 0, "/imagens_comidas/tapioca.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Batata Frita", "100g", "1 porção pequena", 312, 3.4, 41, 15, "/imagens_comidas/batata_frita.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Carboidratos"), "Arroz Integral Cozido", "100g", "Aprox. 4 colheres de sopa", 111, 2.6, 23, 0.9, "/imagens_comidas/arroz_integral.jpg");

                // --- FRUTAS & LEGUMES ---
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Maçã", "1 unidade", "1 unidade média", 95, 0.5, 25, 0.3, "/imagens_comidas/maca.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Banana", "1 unidade", "1 unidade média", 105, 1.3, 27, 0.4, "/imagens_comidas/banana.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Laranja", "1 unidade", "1 unidade média", 62, 1.2, 15, 0.2, "/imagens_comidas/laranja.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Mamão", "1 fatia", "Aprox. 100g", 43, 0.5, 11, 0.3, "/imagens_comidas/mamao.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Uvas", "1 cacho pequeno", "Aprox. 100g", 69, 0.7, 18, 0.2, "/imagens_comidas/uvas.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Pêra", "1 unidade", "1 unidade média", 101, 0.6, 27, 0.2, "/imagens_comidas/pera.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Morango", "1 xícara", "Aprox. 150g", 48, 1, 12, 0.5, "/imagens_comidas/morango.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Tomate", "1 unidade", "1 unidade média", 22, 1, 5, 0.2, "/imagens_comidas/tomate.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Alface", "50g", "5 folhas grandes", 8, 0.7, 1.5, 0.1, "/imagens_comidas/alface.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Brócolis Cozido", "100g", "1 xícara de floretes", 35, 2.4, 7, 0.4, "/imagens_comidas/brocolis.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Cenoura Cozida", "100g", "1 unidade média picada", 35, 0.8, 8, 0.2, "/imagens_comidas/cenoura.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Melancia", "1 fatia", "Aprox. 150g", 45, 1, 11, 0.2, "/imagens_comidas/melancia.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Frutas & Legumes"), "Abacaxi", "1 fatia", "Aprox. 80g", 40, 0.4, 10, 0.1, "/imagens_comidas/abacaxi.jpg");

                // --- GORDURAS BOAS ---
                salvarAlimento(foodItemRepo, categorias.get("Gorduras Boas"), "Abacate", "100g", "1/4 de unidade grande", 160, 2, 9, 15, "/imagens_comidas/abacate.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Gorduras Boas"), "Pasta de Amendoim", "1 colher de sopa", "Aprox. 15g", 94, 4, 3, 8, "/imagens_comidas/pasta_amendoim.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Gorduras Boas"), "Azeite de Oliva Extra Virgem", "1 colher de sopa", "Aprox. 15ml", 119, 0, 0, 14, "/imagens_comidas/azeite.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Gorduras Boas"), "Castanha-do-Pará", "2 unidades", "O suficiente por dia!", 66, 1.4, 1.2, 6.7, "/imagens_comidas/castanha_para.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Gorduras Boas"), "Castanha-de-Caju", "30g", "Um punhado", 165, 5, 9, 13, "/imagens_comidas/castanha_caju.jpg");

                // --- BEBIDAS ---
                salvarAlimento(foodItemRepo, categorias.get("Bebidas"), "Café Preto (sem açúcar)", "1 xícara", "Aprox. 150ml", 2, 0, 0, 0, "/imagens_comidas/cafe.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Bebidas"), "Café com Leite (sem açúcar)", "1 xícara", "Aprox. 150ml", 50, 3, 5, 2, "/imagens_comidas/cafe_leite.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Bebidas"), "Vitamina de Banana", "1 copo (200ml)", "1 copo americano", 150, 4, 30, 2, "/imagens_comidas/vitamina_banana.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Bebidas"), "Vitamina de Abacate", "1 copo (200ml)", "1 copo americano", 220, 3, 15, 18, "/imagens_comidas/vitamina_abacate.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Bebidas"), "Vitamina de Mamão", "1 copo (200ml)", "1 copo americano", 110, 4, 20, 2, "/imagens_comidas/vitamina_mamao.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Bebidas"), "Suco de Maracujá (com açúcar)", "1 copo (300ml)", "1 copo grande", 140, 1, 34, 0, "/imagens_comidas/suco_maracuja.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Bebidas"), "Suco de Abacaxi (com açúcar)", "1 copo (300ml)", "1 copo grande", 100, 1, 25, 0, "/imagens_comidas/suco_abacaxi.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Bebidas"), "Suco de Acerola (com açúcar)", "1 copo (300ml)", "1 copo grande", 46, 1, 11, 0, "/imagens_comidas/suco_acerola.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Bebidas"), "Suco de Cajá (com açúcar)", "1 copo (300ml)", "1 copo grande", 90, 1, 22, 0, "/imagens_comidas/suco_caja.jpg");

                // --- DOCES & SOBREMESAS ---
                salvarAlimento(foodItemRepo, categorias.get("Doces & Sobremesas"), "Achocolatado (Toddy/Nescau)", "1 copo (200ml)", "Leite com 2 colheres de sopa", 200, 7, 28, 6, "/imagens_comidas/achocolatado.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Doces & Sobremesas"), "Iogurte Grego (adoçado)", "1 pote", "Aprox. 100g", 120, 8, 15, 3, "/imagens_comidas/danone.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Doces & Sobremesas"), "Goiabada Cascão", "1 fatia (40g)", "1 fatia grossa", 140, 0.5, 35, 0, "/imagens_comidas/goiabada.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Doces & Sobremesas"), "Bombom", "1 unidade", "Sonho de Valsa, Ouro Branco...", 115, 1, 13, 6, "/imagens_comidas/bombom.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Doces & Sobremesas"), "Barra de Chocolate", "25g", "Aprox. 4 quadradinhos", 135, 1.5, 15, 8, "/imagens_comidas/barra_chocolate.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Doces & Sobremesas"), "Brigadeiro", "1 unidade", "Tamanho de festa", 50, 1, 8, 2, "/imagens_comidas/brigadeiro.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Doces & Sobremesas"), "Açaí na Tigela (com Xarope)", "300g", "1 tigela pequena", 450, 5, 70, 15, "/imagens_comidas/acai-tigela.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Doces & Sobremesas"), "Pudim de Leite", "1 fatia", "1 fatia média", 300, 8, 45, 10, "/imagens_comidas/pudim.jpg");

                // --- SUPLEMENTOS ---
                salvarAlimento(foodItemRepo, categorias.get("Suplementos"), "Whey Protein", "1 scoop (30g)", "1 medidor padrão", 120, 24, 3, 1.5, "/imagens_comidas/whey.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Suplementos"), "Creatina", "1 dose (3g)", "1 medidor padrão", 0, 0, 0, 0, "/imagens_comidas/creatina.jpg");

                // --- DIA DO LIXO ---
                salvarAlimento(foodItemRepo, categorias.get("Dia do Lixo"), "Pizza (Fatia)", "1 fatia grande", "Calabresa, Mussarela, etc.", 350, 15, 35, 15, "/imagens_comidas/pizza.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Dia do Lixo"), "Hambúrguer Grande", "1 unidade", "X-Tudo, Duplo, etc.", 750, 40, 50, 40, "/imagens_comidas/hamburguer_grande.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Dia do Lixo"), "Lasanha à Bolonhesa", "1 porção", "1 prato fundo", 600, 30, 50, 30, "/imagens_comidas/lasanha.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Dia do Lixo"), "Coxinha de Frango", "1 unidade", "Tamanho de lanchonete", 250, 10, 25, 12, "/imagens_comidas/coxinha.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Dia do Lixo"), "Pastel de Carne", "1 unidade", "Tamanho de feira", 350, 12, 30, 20, "/imagens_comidas/pastel.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Dia do Lixo"), "Salgadinho (Cheetos, etc.)", "1 pacote (50g)", "\"Gula\" ou similar", 280, 3, 30, 17, "/imagens_comidas/salgadinho.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Dia do Lixo"), "Coca-Cola", "1 copo (300ml)", "1 copo grande", 137, 0, 35, 0, "/imagens_comidas/coca_cola.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Dia do Lixo"), "Caldo de Cana", "1 copo (300ml)", "1 copo grande", 250, 0, 65, 0, "/imagens_comidas/caldo_de_cana.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Dia do Lixo"), "Vinho Tinto Seco", "1 taça (150ml)", "1 taça padrão", 125, 0.1, 4, 0, "/imagens_comidas/vinho.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Dia do Lixo"), "Cerveja (Skol)", "1 lata (350ml)", "1 lata padrão", 148, 1.5, 13, 0, "/imagens_comidas/cerveja.jpg");
                salvarAlimento(foodItemRepo, categorias.get("Dia do Lixo"), "Pastel de Queijo", "1 unidade", "Tamanho de feira", 400, 15, 30, 25, "/imagens_comidas/pastel_queijo.jpg");

                System.out.println(">>> ALIMENTOS populados com sucesso!");
            }

            // ===================================================================
            // 2. GRUPOS MUSCULARES + EXERCÍCIOS (roda só se estiver vazio)
            // ===================================================================
            if (muscleGroupRepo.count() == 0) {
                System.out.println(">>> Populando GRUPOS MUSCULARES e EXERCÍCIOS...");

                MuscleGroup peito = salvarGrupo(muscleGroupRepo, "Peito");
                MuscleGroup costas = salvarGrupo(muscleGroupRepo, "Costas");
                MuscleGroup pernas = salvarGrupo(muscleGroupRepo, "Pernas");
                MuscleGroup ombros = salvarGrupo(muscleGroupRepo, "Ombros");
                MuscleGroup bracos = salvarGrupo(muscleGroupRepo, "Braços");

                // --- PEITO ---
                salvarExercicio(exerciseRepo, peito, "Supino Reto com Barra", "O clássico construtor de peitoral.", "/videos_treinos/supino-reto-com-barra.mp4", "/imagens_biblioteca/supino.jpg");
                salvarExercicio(exerciseRepo, peito, "Supino Inclinado", "Foco na porção superior do peitoral.", "/videos_treinos/inclinado.mp4", "/imagens_biblioteca/frontal-card.jpg");
                salvarExercicio(exerciseRepo, peito, "Crucifixo (Cross)", "Isolamento do peitoral no cabo.", "/videos_treinos/cro.mp4", "/imagens_biblioteca/cross.jpg");
                salvarExercicio(exerciseRepo, peito, "Crucifixo Inclinado", "Trabalha a parte superior do peito com maior amplitude.", "/videos_treinos/cur.mp4", "/imagens_biblioteca/cruci.jpg");
                salvarExercicio(exerciseRepo, peito, "Mergulho (Paralelas)", "Excelente para peitoral inferior e tríceps.", "/videos_treinos/mergulho.mp4", "/imagens_biblioteca/mergulho.jpg");

                // --- COSTAS ---
                salvarExercicio(exerciseRepo, costas, "Remada Curvada", "Constrói espessura nas costas.", "/videos_treinos/re.mp4", "/imagens_biblioteca/remada.jpg");
                salvarExercicio(exerciseRepo, costas, "Remada Baixa (Polia)", "Foco na parte média das costas.", "/videos_treinos/ro.mp4", "/imagens_biblioteca/remada1.jpg");
                salvarExercicio(exerciseRepo, costas, "Levantamento Terra Romeno", "Trabalha posterior de coxa e lombar.", "/videos_treinos/levantamento-terra-romeno.mp4", "/imagens_biblioteca/terra-romeno-card.jpg");
                salvarExercicio(exerciseRepo, costas, "Stiff com Barra", "Foco em posterior de coxa e glúteo.", "/videos_treinos/stiff-com-barra.mp4", "/imagens_biblioteca/stiff-card.jpg");
                salvarExercicio(exerciseRepo, costas, "Good Morning", "Fortalece lombar e posterior de coxa.", "/videos_treinos/ros.mp4", "/imagens_biblioteca/goodmorning.jpg");
                salvarExercicio(exerciseRepo, costas, "Puxada Alta na Polia", "Foco principal em dorsais.", "/videos_treinos/pux.mp4", "/imagens_biblioteca/puxada.jpg");

                // --- PERNAS ---
                salvarExercicio(exerciseRepo, pernas, "Agachamento Búlgaro", "Unilateral, ótimo pra quadríceps e glúteo.", "/videos_treinos/agachamento-bulgaro.mp4", "/imagens_biblioteca/agachamento-card.jpg");
                salvarExercicio(exerciseRepo, pernas, "Agachamento Livre", "O rei dos exercícios de perna.", "/videos_treinos/agacholivre.mp4", "/imagens_biblioteca/again.jpg");
                salvarExercicio(exerciseRepo, pernas, "Leg Press", "Trabalha quadríceps com segurança.", "/videos_treinos/leg-press.mp4", "/imagens_biblioteca/leg-card.jpg");
                salvarExercicio(exerciseRepo, pernas, "Leg Press Sumô", "Variação com pés afastados, foco interno da coxa.", "/videos_treinos/leg-sumoxd.mp4", "/imagens_biblioteca/legsumo.jpg");
                salvarExercicio(exerciseRepo, pernas, "Cadeira Extensora", "Isolamento de quadríceps.", "/videos_treinos/cadeira-extensora.mp4", "/imagens_biblioteca/cadeira-card.jpg");
                salvarExercicio(exerciseRepo, pernas, "Cadeira Abdutora", "Foco em glúteo médio.", "/videos_treinos/abdutora.mp4", "/imagens_biblioteca/abdutora-card.jpg");
                salvarExercicio(exerciseRepo, pernas, "Cadeira Adutora", "Foco em adutores (parte interna da coxa).", "/videos_treinos/adutora.mp4", "/imagens_biblioteca/adutora-card.jpg");
                salvarExercicio(exerciseRepo, pernas, "Mesa Flexora", "Isolamento de posterior de coxa.", "/videos_treinos/inv.mp4", "/imagens_biblioteca/mesa-flexora-card.jpg");
                salvarExercicio(exerciseRepo, pernas, "Panturrilha Sentado", "Isolamento de panturrilha (sóleo).", "/videos_treinos/panturrilha-sentado.mp4", "/imagens_biblioteca/pantu-leg.jpg");
                salvarExercicio(exerciseRepo, pernas, "Panturrilha em Pé", "Foco na panturrilha (gastrocnêmio).", "/videos_treinos/panturrilha-em-pe.mp4", "/imagens_biblioteca/pantu.jpg");
                salvarExercicio(exerciseRepo, pernas, "Panturrilha no Leg Press", "Variação de panturrilha no aparelho de leg press.", "/videos_treinos/panturrilha-no-leg-press.mp4", "/imagens_biblioteca/panturrilha.jpg");

                // --- OMBROS ---
                salvarExercicio(exerciseRepo, ombros, "Desenvolvimento com Halteres", "Constrói a musculatura do ombro como um todo.", "/videos_treinos/desen.mp4", "/imagens_biblioteca/desenvolvimento-card.jpg");
                salvarExercicio(exerciseRepo, ombros, "Elevação Lateral", "Isolamento do deltoide lateral.", "/videos_treinos/alta.mp4", "/imagens_biblioteca/elevacao-card.jpg");
                salvarExercicio(exerciseRepo, ombros, "Elevação de Quadril (Hip Thrust)", "Foco em glúteo, mas classificado aqui no seed original.", "/videos_treinos/elevacao-de-quadril.mp4", "/imagens_biblioteca/elevacao-de-quadril-card.jpg");
                salvarExercicio(exerciseRepo, ombros, "Desenvolvimento Arnold", "Variação que recruta todas as porções do ombro.", "/videos_treinos/ante.mp4", "/imagens_biblioteca/arnold-card.jpg");
                salvarExercicio(exerciseRepo, ombros, "Elevação Frontal", "Isolamento do deltoide anterior.", "/videos_treinos/front.mp4", "/imagens_biblioteca/frontal-card.jpg");

                // --- BRAÇOS ---
                salvarExercicio(exerciseRepo, bracos, "Rosca Direta", "Constrói o bíceps.", "/videos_treinos/rosca.mp4", "/imagens_biblioteca/rosca-card.jpg");
                salvarExercicio(exerciseRepo, bracos, "Rosca Martelo", "Foco em bíceps e antebraço.", "/videos_treinos/mar.mp4", "/imagens_biblioteca/martelo.jpg");
                salvarExercicio(exerciseRepo, bracos, "Tríceps Testa", "Isolamento do tríceps.", "/videos_treinos/tes.mp4", "/imagens_biblioteca/testa.jpg");
                salvarExercicio(exerciseRepo, bracos, "Tríceps Coice no Cabo", "Isolamento do tríceps na polia.", "/videos_treinos/coice-no-cabo.mp4", "/imagens_biblioteca/coice-card.jpg");
                salvarExercicio(exerciseRepo, bracos, "Tríceps Corda", "Isolamento do tríceps com corda na polia.", "/videos_treinos/corda.mp4", "/imagens_biblioteca/corda.jpg");
                salvarExercicio(exerciseRepo, bracos, "Rosca Unilateral", "Rosca bíceps feita um braço de cada vez.", "/videos_treinos/unilateral.mp4", "/imagens_biblioteca/unilateral.jpg");

                System.out.println(">>> GRUPOS MUSCULARES e EXERCÍCIOS populados com sucesso!");
            }
        };
    }

    // --- Métodos auxiliares, só pra não repetir código toda hora ---

    private MuscleGroup salvarGrupo(MuscleGroupRepository repo, String nome) {
        MuscleGroup grupo = new MuscleGroup();
        grupo.setName(nome);
        return repo.save(grupo);
    }

    private void salvarExercicio(ExerciseRepository repo, MuscleGroup grupo, String nome, String descricao,
                                  String videoPath, String imagePath) {
        Exercise ex = new Exercise();
        ex.setName(nome);
        ex.setDescription(descricao);
        ex.setLocalVideoPath(videoPath);
        ex.setImagePath(imagePath);
        ex.setMuscleGroup(grupo);
        repo.save(ex);
    }

    private void salvarAlimento(FoodItemRepository repo, FoodCategory categoria, String nome, String unidade,
                                 String servingDesc, double calorias, double proteina, double carbo, double gordura,
                                 String imagePath) {
        FoodItem item = new FoodItem();
        item.setName(nome);
        item.setUnit(unidade);
        item.setServingDesc(servingDesc);
        item.setCalories(calorias);
        item.setProtein(proteina);
        item.setCarbs(carbo);
        item.setFat(gordura);
        item.setImagePath(imagePath);
        item.setCategory(categoria);
        repo.save(item);
    }
}