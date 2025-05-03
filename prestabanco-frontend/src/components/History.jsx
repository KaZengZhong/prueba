import React, { useEffect, useState } from 'react';
import { 
  Box, 
  Container, 
  Typography, 
  Paper, 
  List, 
  ListItem, 
  ListItemText 
} from '@mui/material';
import ApplicationService from '../services/application.service';

const SolicitudesClientes = () => {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchApplications = async () => {
      try {
        const response = await ApplicationService.getAll();
        setApplications(response.data);
      } catch (err) {
        setError('Error al cargar las solicitudes');
        console.error('Error:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchApplications();
  }, []);

  // Agrupar solicitudes por cliente
  const clientesConSolicitudes = {};
  
  applications.forEach(app => {
    const clienteNombre = `${app.user.firstName} ${app.user.lastName}`;
    
    if (!clientesConSolicitudes[clienteNombre]) {
      clientesConSolicitudes[clienteNombre] = {
        aprobadas: [],
        rechazadas: [],
        canceladas: []
      };
    }
    
    if (app.status === 'APPROVED' || app.status === 'IN_DISBURSEMENT') {
      clientesConSolicitudes[clienteNombre].aprobadas.push(app);
    } else if (app.status === 'REJECTED') {
      clientesConSolicitudes[clienteNombre].rechazadas.push(app);
    } else if (app.status === 'CANCELLED') {
      clientesConSolicitudes[clienteNombre].canceladas.push(app);
    }
  });

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <Typography>Cargando solicitudes...</Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <Typography color="error">{error}</Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ 
        position: 'absolute',
        width: '100%',
        left: 0,
        minHeight: '100vh',
        bgcolor: 'background.default',
        pt: 10,
        pb: 4
    }}>
      <Container maxWidth="md">
        <Typography variant="h4" gutterBottom sx={{ mb: 4 }}>
          Historial de solicitudes
        </Typography>
        
        {Object.keys(clientesConSolicitudes).length === 0 ? (
          <Typography>No hay solicitudes disponibles</Typography>
        ) : (
          Object.entries(clientesConSolicitudes).map(([cliente, solicitudes], index) => (
            <Paper 
              key={index} 
              elevation={3} 
              sx={{ 
                mb: 3, 
                p: 3, 
                border: '2px solid', // Borde más grueso
                borderColor: 'primary.main', // Color del borde
                borderRadius: 2, // Bordes redondeados
                boxShadow: 4 // Sombra para mayor destaque
              }}
            >
              <Box sx={{ bgcolor: 'info.main', p: 2, borderRadius: 1 }}>
                <Typography variant="h6" sx={{ color: 'white' }}>
                  Cliente: {cliente}
                </Typography>
              </Box>

              <Typography variant="subtitle1" sx={{ mt: 2 }}>
                Solicitudes Aprobadas ({solicitudes.aprobadas.length})
              </Typography>
              {solicitudes.aprobadas.length > 0 ? (
                <List sx={{ bgcolor: 'success.light', p: 1, borderRadius: 1 }}>
                  {solicitudes.aprobadas.map((app) => (
                    <ListItem key={app.id} divider>
                      <ListItemText
                        primary={`Solicitud #${app.id}`}
                        secondary={`Tipo: ${getLoanTypeName(app.propertyType)} - Monto: $${app.requestedAmount?.toLocaleString()}`}
                      />
                    </ListItem>
                  ))}
                </List>
              ) : (
                <Typography variant="body2" color="text.secondary" sx={{ ml: 2 }}>
                  No hay solicitudes aprobadas
                </Typography>
              )}

              <Typography variant="subtitle1" sx={{ mt: 3 }}>
                Solicitudes Rechazadas ({solicitudes.rechazadas.length})
              </Typography>
              {solicitudes.rechazadas.length > 0 ? (
                <List sx={{ bgcolor: 'error.light', p: 1, borderRadius: 1 }}>
                  {solicitudes.rechazadas.map((app) => (
                    <ListItem key={app.id} divider>
                      <ListItemText
                        primary={`Solicitud #${app.id}`}
                        secondary={`Tipo: ${getLoanTypeName(app.propertyType)} - Monto: $${app.requestedAmount?.toLocaleString()}`}
                      />
                    </ListItem>
                  ))}
                </List>
              ) : (
                <Typography variant="body2" color="text.secondary" sx={{ ml: 2 }}>
                  No hay solicitudes rechazadas
                </Typography>
              )}

              <Typography variant="subtitle1" sx={{ mt: 3 }}>
                Solicitudes Canceladas ({solicitudes.canceladas.length})
              </Typography>
              {solicitudes.canceladas.length > 0 ? (
                <List sx={{ bgcolor: 'grey.400', p: 1, borderRadius: 1 }}>
                  {solicitudes.canceladas.map((app) => (
                    <ListItem key={app.id} divider>
                      <ListItemText
                        primary={`Solicitud #${app.id}`}
                        secondary={`Tipo: ${getLoanTypeName(app.propertyType)} - Monto: $${app.requestedAmount?.toLocaleString()}`}
                      />
                    </ListItem>
                  ))}
                </List>
              ) : (
                <Typography variant="body2" color="text.secondary" sx={{ ml: 2 }}>
                  No hay solicitudes canceladas
                </Typography>
              )}
            </Paper>
          ))
        )}
      </Container>
    </Box>
  );
};

// Función auxiliar para mostrar nombres de tipo de préstamo
const getLoanTypeName = (type) => {
  const types = {
    'FIRST_HOME': 'Primera Vivienda',
    'SECOND_HOME': 'Segunda Vivienda',
    'COMMERCIAL': 'Propiedad Comercial',
    'REMODELING': 'Remodelación'
  };
  return types[type] || type;
};

export default SolicitudesClientes;