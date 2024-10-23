import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getToken } from "../services/localStorageService";
import profileService from "../services/profileService";
import keycloak from "../keycloak";
import {
  Box,
  Card,
  CircularProgress,
  Typography,
  Avatar,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
} from "@mui/material";
import EmailIcon from '@mui/icons-material/Email';
import PersonIcon from '@mui/icons-material/Person';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';

export default function Profile() {
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState(null);
  const [loading, setLoading] = useState(true);

  const getUserDetails = async () => {
    try {
      const response = await profileService.getMyInfo();
      if (response) {
        setUserDetails(response.data.result);
      }
    } catch (error) {
      console.error("Error fetching user details:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const accessToken = keycloak.token;
    if (!accessToken) {
      navigate("/login");
    } else {
      getUserDetails();
    }
  }, [navigate]);

  if (loading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh" }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ bgcolor: '#f0f2f5', minHeight: "100vh", pt: 4 }}>
      <Box sx={{ maxWidth: 600, margin: "0 auto" }}>
        <Card sx={{ p: 3 }}>
          <Box sx={{ display: "flex", alignItems: "center", mb: 3 }}>
            <Avatar
              sx={{
                width: 100,
                height: 100,
                mr: 3,
                bgcolor: '#1976d2',
              }}
            >
              {userDetails?.username?.charAt(0).toUpperCase()}
            </Avatar>
            <Box>
              <Typography variant="h4">
                {userDetails?.username}
              </Typography>
              <Typography variant="body1" color="text.secondary">
                {userDetails?.firstName} {userDetails?.lastName}
              </Typography>
            </Box>
          </Box>
          
          <List>
            <ListItem>
              <ListItemIcon>
                <EmailIcon />
              </ListItemIcon>
              <ListItemText 
                primary="Email" 
                secondary={userDetails?.email}
              />
            </ListItem>
            <ListItem>
              <ListItemIcon>
                <PersonIcon />
              </ListItemIcon>
              <ListItemText 
                primary="Username" 
                secondary={userDetails?.username}
              />
            </ListItem>
            <ListItem>
              <ListItemIcon>
                <CalendarTodayIcon />
              </ListItemIcon>
            </ListItem>
          </List>
        </Card>
      </Box>
    </Box>
  );
}