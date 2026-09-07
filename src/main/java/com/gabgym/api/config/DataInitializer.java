package com.gabgym.api.config;

import com.gabgym.api.entities.Exercise;
import com.gabgym.api.entities.FoodCategory;
import com.gabgym.api.entities.MuscleGroup;
import com.gabgym.api.repositories.ExerciseRepository;
import com.gabgym.api.repositories.FoodCategoryRepository;
import com.gabgym.api.repositories.MuscleGroupRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(MuscleGroupRepository muscleGroupRepo, 
                                      ExerciseRepository exerciseRepo,
                                      FoodCategoryRepository categoryRepo) {
        return args -> {
            // Só injeta se a tabela de grupos musculares estiver zerada
            if (muscleGroupRepo.count() == 0) {
                
                // 1. Criando os Grupos Musculares
                MuscleGroup peito = new MuscleGroup();
                peito.setName("Peito");

                MuscleGroup costas = new MuscleGroup();
                costas.setName("Costas");

                MuscleGroup pernas = new MuscleGroup();
                pernas.setName("Pernas");

                muscleGroupRepo.saveAll(List.of(peito, costas, pernas));

                // 2. Criando os Exercícios e linkando com os Grupos
                Exercise supino = new Exercise();
                supino.setName("Supino Reto");
                supino.setDescription("O clássico construtor de peitoral.");
                supino.setMuscleGroup(peito);

                Exercise puxada = new Exercise();
                puxada.setName("Puxada Alta na Polia");
                puxada.setDescription("Foco principal em dorsais.");
                puxada.setMuscleGroup(costas);

                Exercise agachamento = new Exercise();
                agachamento.setName("Agachamento Livre");
                agachamento.setDescription("O rei dos exercícios de perna.");
                agachamento.setMuscleGroup(pernas);

                exerciseRepo.saveAll(List.of(supino, puxada, agachamento));

                // 3. Criando algumas Categorias de Alimentos pra testar a dieta
                FoodCategory carbos = new FoodCategory();
                carbos.setName("Carboidratos");

                FoodCategory proteinas = new FoodCategory();
                proteinas.setName("Proteínas");

                categoryRepo.saveAll(List.of(carbos, proteinas));

                System.out.println("SUCESSO: Banco de dados populado com a carga inicial no padrão BR!");
            }
        };
    }
}