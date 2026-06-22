package proyecto.Ruta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import proyecto.Ruta.modelo.HorarioDTO;
import proyecto.Ruta.modelo.ModeloRuta;
import proyecto.Ruta.repository.RutaRepository;

import java.util.List;

@Service
public class RutaService {
    @Autowired
    private RutaRepository repository;
     @Autowired
    private RestTemplate restTemplate;

    @Value("${horario.service.url:http://localhost:8082/api/horarios}")
    private String horarioServiceUrl;

     public List<ModeloRuta> listarRutas(){
         return repository.findAll();
     }
    public List<HorarioDTO> obtenerHorariosActualizados() {

        ResponseEntity<List<HorarioDTO>> response =
                restTemplate.exchange(
                        horarioServiceUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<HorarioDTO>>() {}
                );

        return response.getBody();
    }
    public String actualizarEstadoRutas() {

        List<ModeloRuta> rutas = repository.findAll();

        for (ModeloRuta ruta : rutas) {

            ruta.setEstado("AJUSTADA");

            ruta.setTiempoEstimado(
                    ruta.getTiempoEstimado() + 15
            );

            repository.save(ruta);
        }

        return "Rutas actualizadas correctamente";
    }

}
