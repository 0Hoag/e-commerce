import React, { useState, useEffect } from "react";
import {
  Box,
  Button,
  Card,
  CircularProgress,
  Snackbar,
  TextField,
  Typography,
} from "@mui/material";
import { Alert } from "@mui/material";
import { useNavigate } from "react-router-dom";
import profileService from "../services/profileService";
import Header from "../components/Header";
import Scene from "./Scene";
import keycloak from "../keycloak";

export default function Settings() {
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState(null);
  const [editing, setEditing] = useState(false);
  const [password, setPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [snackType, setSnackType] = useState("error");
  
  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setSnackBarOpen(false);
  };

  const showError = (message) => {
    setSnackType("error");
    setSnackBarMessage(message);
    setSnackBarOpen(true);
  };

  const showSuccess = (message) => {
    setSnackType("success");
    setSnackBarMessage(message);
    setSnackBarOpen(true);
  };

  const getUserDetails = async () => {
    try {
      const response = await profileService.getMyInfo();
      const userInfo = response.data.result;

      setUserDetails(userInfo);
    } catch (error) {
      showError(error.message);
    }
  };

  const handleUpdateInfo = async () => {
    try {
        
      showError("You need to set a password before changing it.");
      setEditing(false); // Đóng chế độ chỉnh sửa
    }catch (error) {

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

  if (!userDetails) {
    return (
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          gap: "30px",
          justifyContent: "center",
          alignItems: "center",
          height: "100vh",
          bgcolor: "#121212", // Dark background
          color: "#e0e0e0", // Light text color
        }}
      >
        <CircularProgress color="inherit" />
        <Typography color="#e0e0e0">Loading ...</Typography>
      </Box>
    );
  }

  return (
    <Scene>
      <Header />
      <Snackbar
        open={snackBarOpen}
        autoHideDuration={6000}
        onClose={handleCloseSnackBar}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={handleCloseSnackBar}
          severity={snackType}
          variant="filled"
          sx={{ width: "100%" }}
        >
          {snackBarMessage}
        </Alert>
      </Snackbar>
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        height="100vh"
        bgcolor="#1e1e1e" // Dark background
        color="#e0e0e0" // Light text color
      >
        <Card
          sx={{
            width: "90%",
            maxWidth: 800,
            boxShadow: 3,
            borderRadius: 2,
            padding: 4,
            bgcolor: "#1e1e1e", // Slightly lighter dark grey for the card
            color: "#e0e0e0", // Light text color
          }}
        >
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "flex-start",
              width: "100%",
              gap: "10px",
            }}
          >
            <Typography
              sx={{
                fontSize: 18,
                mb: "40px",
                color: "#b0b0b0", // Lighter gray text color for email
              }}
            >
              Settings for {userDetails.username}
            </Typography>
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#b0b0b0", // Lighter gray text color for email
                }}
              >
                User Id
              </Typography>
              <Typography
                sx={{
                  fontSize: 14,
                  color: "#b0b0b0", // Lighter gray text color for email
                }}
              >
                {userDetails.userId}
              </Typography>
            </Box>
            
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#b0b0b0", // Lighter gray text color for email
                }}
              >
                Email
              </Typography>
              <Typography
                sx={{
                  fontSize: 14,
                  color: "#b0b0b0", // Lighter gray text color for email
                }}
              >
                {userDetails.email}
              </Typography>
            </Box>

            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#b0b0b0", // Lighter gray text color for email

                }}
              >
                First Name
              </Typography>
              {editing ? (
                <TextField
                  value={userDetails.firstName}
                  onChange={(e) =>
                    setUserDetails({
                      ...userDetails,
                      firstName: e.target.value,
                    })
                  }
                />
              ) : (
                <Typography>{userDetails.firstName}</Typography>
              )}
            </Box>
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#b0b0b0", // Lighter gray text color for email
                }}
              >
                Last Name
              </Typography>
              {editing ? (
                <TextField
                  value={userDetails.lastName}
                  onChange={(e) =>
                    setUserDetails({
                      ...userDetails,
                      lastName: e.target.value,
                    })
                  }
                />
              ) : (
                <Typography>{userDetails.lastName}</Typography>
              )}
            </Box>
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "flex-start",
                width: "100%",
              }}
            >
              <Typography
                sx={{
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#b0b0b0", // Lighter gray text color for email
                }}
              >
                Date of birth
              </Typography>
              {editing ? (
                <TextField
                  value={userDetails.dob}
                  onChange={(e) =>
                    setUserDetails({
                      ...userDetails,
                      dob: e.target.value,
                    })
                  }
                />
              ) : (
                <Typography>{userDetails.dob}</Typography>
              )}
            </Box>
            {editing ? (
              <Button
                onClick={handleUpdateInfo}
                variant="contained"
                color="primary"
                size="large"
                sx={{ mt: 2 }}
              >
                Save Changes
              </Button>
            ) : (
              <Button
                onClick={() => setEditing(true)}
                variant="outlined"
                color="primary"
                size="large"
                sx={{ mt: 2 }}
              >
                Edit Profile
              </Button>
            )}
            <Box
              component="form"
              sx={{
                display: "flex",
                flexDirection: "column",
                gap: "10px",
                width: "100%",
                mt: 3,
              }}
            >
              <Typography>Change Avatar</Typography>
              {/* Assume there's an input for uploading avatar */}
              <Button
                variant="contained"
                color="primary"
                size="large"
                fullWidth
              >
                Upload Avatar
              </Button>
            </Box>
          </Box>
        </Card>
      </Box>
    </Scene>
  );
}

