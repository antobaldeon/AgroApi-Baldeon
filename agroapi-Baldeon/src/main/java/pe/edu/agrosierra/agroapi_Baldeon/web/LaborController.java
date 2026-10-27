package pe.edu.agrosierra.agroapi_Baldeon.web;

import jakarta.validation.Valid;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.Labor;
import pe.edu.agrosierra.agroapi_Baldeon.servicio.LaborService;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.ActualizarLaborRequest;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.LaborResponse;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.PaginaResponse;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.RegistrarLaborRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
public class LaborController {

    private final LaborService laborService;

    public LaborController(LaborService laborService) {
        this.laborService = laborService;
    }

    @GetMapping("/lotes/{loteId}/labores")
    public PaginaResponse<LaborResponse> listarPorLote(
            @PathVariable Long loteId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio) {
        Page<Labor> resultado = laborService.listarPorLote(loteId, pagina, tamanio);
        return PaginaResponse.desde(resultado, LaborResponse::desde);
    }

    @PostMapping("/labores")
    public ResponseEntity<LaborResponse> registrar(
            @Valid @RequestBody RegistrarLaborRequest request, Authentication authentication) {
        Labor creada = laborService.registrar(request, usuarioId(authentication));
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/labores/{id}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(LaborResponse.desde(creada));
    }

    @PutMapping("/labores/{id}")
    public LaborResponse actualizar(@PathVariable Long id,
                                    @Valid @RequestBody ActualizarLaborRequest request,
                                    Authentication authentication) {
        // El usuario se pasa a la capa de servicio para aplicar RN-02.
        Labor actualizada = laborService.actualizar(id, request, usuarioId(authentication), authentication);
        return LaborResponse.desde(actualizada);
    }

    @DeleteMapping("/labores/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        laborService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Long usuarioId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
