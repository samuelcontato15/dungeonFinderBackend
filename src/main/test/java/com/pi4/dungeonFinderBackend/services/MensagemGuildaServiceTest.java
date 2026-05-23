package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MensagemGuildaServiceTest {

    @Autowired
    private MensagemGuildaService mensagemGuildaService;

    @Autowired
    private MembroGuildaService membroGuildaService;

    @Autowired
    private GuildaService guildaService;

    @Autowired
    private JogoService jogoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UUID adminId;
    private UUID liderId;
    private UUID membroId;
    private UUID foraId;
    private UUID guildaId;

    @BeforeEach
    void setUp() {
        adminId = usuarioRepository.findByEmail("admin@admin.com")
                .orElseThrow(() -> new RuntimeException("Admin padrão não encontrado"))
                .getId();

        Jogo jogo = new Jogo();
        jogo.setNome("Jogo Mensagem");
        jogo.setSlug("jogo-mensagem");
        jogo.setCapa("");
        jogo.setCategoria(CategoriaJogo.MMO);
        UUID jogoId = jogoService.criar(jogo, adminId).getId();

        Usuario lider = new Usuario();
        lider.setNick("lider_msg");
        lider.setEmail("lider@msg.com");
        lider.setSenhaHash("123");
        liderId = usuarioService.criar(lider).getId();

        Usuario membro = new Usuario();
        membro.setNick("membro_msg");
        membro.setEmail("membro@msg.com");
        membro.setSenhaHash("123");
        membroId = usuarioService.criar(membro).getId();

        Usuario fora = new Usuario();
        fora.setNick("fora_msg");
        fora.setEmail("fora@msg.com");
        fora.setSenhaHash("123");
        foraId = usuarioService.criar(fora).getId();

        Guilda guilda = new Guilda();
        guilda.setNome("Guilda Mensagem");
        guilda.setDescricao("Desc");
        guilda.setBanner("");
        guilda.setMaxMembros(10);
        guildaId = guildaService.criar(guilda, jogoId, liderId).getId();

        membroGuildaService.entrar(guildaId, membroId);
    }

    @Test
    void enviar_comSucesso() {
        MensagemGuilda msg = mensagemGuildaService.enviar(guildaId, "Olá guilda!", liderId);
        assertThat(msg.getId()).isNotNull();
        assertThat(msg.getConteudo()).isEqualTo("Olá guilda!");
    }

    @Test
    void enviar_vazia_lancaExcecao() {
        assertThatThrownBy(() -> mensagemGuildaService.enviar(guildaId, "", liderId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Mensagem não pode ser vazia");
    }

    @Test
    void enviar_muitoLonga_lancaExcecao() {
        String longa = "a".repeat(301);
        assertThatThrownBy(() -> mensagemGuildaService.enviar(guildaId, longa, liderId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Mensagem não pode ter mais de 300 caracteres");
    }

    @Test
    void enviar_foraDaGuilda_lancaExcecao() {
        assertThatThrownBy(() -> mensagemGuildaService.enviar(guildaId, "Olá!", foraId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Você não é membro desta guilda");
    }

    @Test
    void listarMensagens_retornaMensagensOrdenadas() {
        mensagemGuildaService.enviar(guildaId, "Primeira", liderId);
        mensagemGuildaService.enviar(guildaId, "Segunda", membroId);
        List<MensagemGuilda> msgs = mensagemGuildaService.listarMensagens(guildaId);
        assertThat(msgs).hasSize(2);
        assertThat(msgs.get(0).getConteudo()).isEqualTo("Primeira");
    }

    @Test
    void deletar_peloDono_comSucesso() {
        MensagemGuilda msg = mensagemGuildaService.enviar(guildaId, "Deletar", membroId);
        mensagemGuildaService.deletar(msg.getId(), membroId);
        assertThat(mensagemGuildaService.listarMensagens(guildaId)).isEmpty();
    }

    @Test
    void deletar_peloLider_comSucesso() {
        MensagemGuilda msg = mensagemGuildaService.enviar(guildaId, "Deletar pelo lider", membroId);
        mensagemGuildaService.deletar(msg.getId(), liderId);
        assertThat(mensagemGuildaService.listarMensagens(guildaId)).isEmpty();
    }

    @Test
    void deletar_peloAdminGeral_comSucesso() {
        MensagemGuilda msg = mensagemGuildaService.enviar(guildaId, "Deletar pelo admin", membroId);
        mensagemGuildaService.deletar(msg.getId(), adminId);
        assertThat(mensagemGuildaService.listarMensagens(guildaId)).isEmpty();
    }

    @Test
    void deletar_semPermissao_lancaExcecao() {
        MensagemGuilda msg = mensagemGuildaService.enviar(guildaId, "Sem permissão", liderId);
        // membro tenta deletar mensagem do lider
        assertThatThrownBy(() -> mensagemGuildaService.deletar(msg.getId(), membroId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Sem permissão para deletar esta mensagem");
    }

    @Test
    void deletar_inexistente_lancaExcecao() {
        assertThatThrownBy(() -> mensagemGuildaService.deletar(UUID.randomUUID(), liderId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Mensagem não encontrada");
    }
}