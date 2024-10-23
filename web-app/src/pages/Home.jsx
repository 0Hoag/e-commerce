import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Scene from "./Scene";
import profileService from "../services/profileService";
import productService from "../services/productService";
import { Box, Typography, CircularProgress, Snackbar, Alert, Grid } from "@mui/material";
import ProductCard from "./product/ProductCard";
import keycloak from "../keycloak";

export default function Home() {
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [snackType, setSnackType] = useState("error");
  const [products, setProducts] = useState([]);

  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setSnackBarOpen(false);
  };

  const showMessage = (message, type = "error") => {
    setSnackType(type);
    setSnackBarMessage(message);
    setSnackBarOpen(true);
  };

  const getUserDetails = async () => {
    try {
      const response = await profileService.getMyProfile();
      console.log("response: ", response);
      setUserDetails(response.data.result);
    } catch (error) {
      showMessage(error.message);
    }
  };

  const getProducts = async () => {
    try {
      const response = await productService.getAllProduct();
      setProducts(Array.isArray(response.data.result) ? response.data.result : []);
    } catch (error) {
      showMessage(error.message);
      setProducts([]);
    }
  };

  useEffect(() => {
    if (!keycloak.authenticated) {
      navigate("/login");
    } else {
      console.log("User token: ", keycloak.token); 
      console.log("get All profile: ", profileService.getAllProfile());
    }

    const fetchData = async () => {
      try {
        await Promise.all([getUserDetails(), getProducts()]);
      } catch (error) {
        console.error("Error fetching data:", error);
        showMessage("An error occurred while fetching data.");
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [navigate]);

  const handleAddToCart = (product) => {
    // Logic to add product to cart
    console.log("Added to cart:", product);
  };

  const handleBuyNow = (product) => {
    // Logic for buying the product
    console.log("Buying now:", product);
  };

  return (
    <Scene>
      <Snackbar
        open={snackBarOpen}
        onClose={handleCloseSnackBar}
        autoHideDuration={6000}
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
      {loading ? (
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            gap: "30px",
            justifyContent: "center",
            alignItems: "center",
            height: "100vh",
            bgcolor: "#1e1e1e",
            color: "#e0e0e0",
          }}
        >
          <CircularProgress color="inherit" />
          <Typography>Đang tải...</Typography>
        </Box>
      ) : (
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            padding: "20px",
            bgcolor: "#1e1e1e",
            color: "#e0e0e0",
            minHeight: "100vh",
          }}
        >
          <Typography variant="h4" sx={{ marginBottom: "20px" }}>
            Welcome, {userDetails?.username || "User"}!
          </Typography>
          <Typography variant="body1" sx={{ marginBottom: "20px" }}>
            This is your home page. You can see product details below.
          </Typography>
          
          {/* Product Detail Section */}
          <Box sx={{ width: '100%', maxWidth: '1200px' }}>
            <Typography variant="h5" sx={{ marginBottom: "15px" }}>
              All Products
            </Typography>
            {products.length > 0 ? (
              <Grid container spacing={3}>
                {products.map((product) => (
                  <Grid item xs={12} sm={6} md={4} key={product.id}>
                    <ProductCard 
                      product={product} 
                      onAddToCart={handleAddToCart} 
                      onBuyNow={handleBuyNow} 
                    />
                  </Grid>
                ))}
              </Grid>
            ) : (
              <Typography>No products available.</Typography>
            )}
          </Box>
        </Box>
      )}
    </Scene>
  );
}