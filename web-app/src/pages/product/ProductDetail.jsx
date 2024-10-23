import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Box, Typography, CircularProgress, Snackbar, Alert, Button, Grid, Paper } from "@mui/material";
import productService from "../../services/productService";
import Scene from "../Scene";
import profileService from "../../services/profileService";
import cartItemService from "../../services/cartItemService";

const ProductDetail = () => {
  const { id } = useParams(); // Get the product ID from the URL
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState("");
  const [snackType, setSnackType] = useState("error");

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

  const getProductDetails = async () => {
    try {
      const response = await productService.getProduct(id); // Fetch product by ID
      if (response.data.code === 1000) {
        setProduct(response.data.result);
      } else {
        showMessage("Product not found");
      }
    } catch (error) {
      showMessage(error.message);
    } finally {
      setLoading(false);
    }
  };

  const addToCart = async () => {
    try {
      const myInfo = await profileService.getMyProfile();
      const userId = myInfo.data.result.profileId;

      const newCartItem = {
        quantity: 1,
        productId: id,
        profileId: userId
      };

      // Logic to add the item to the cart
      const response = await cartItemService.createCartItem(newCartItem);
      await cartItemService.addCartItem(userId, [response.data.result.cartItemId]);
      showMessage("Item added to cart successfully!", "success");
    } catch (error) {
      showMessage("Error adding item to cart: " + error.message);
    }
  };

  useEffect(() => {
    getProductDetails();
  }, [id]);

  if (loading) {
    return (
      <Scene>
        <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh", bgcolor: "#1e1e1e", color: "#e0e0e0" }}>
          <CircularProgress color="inherit" />
        </Box>
      </Scene>
    );
  }

  if (!product) {
    return <Typography>No product details available.</Typography>;
  }

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
      <Box sx={{ padding: 3, bgcolor: '#1e1e1e', color: '#e0e0e0' }}>
        <Typography variant="h4" gutterBottom>
          {product.name}
        </Typography>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <img src={product.image || 'https://via.placeholder.com/400'} alt={product.name} style={{ width: '100%', maxHeight: '400px', objectFit: 'cover', borderRadius: '8px' }} />
          </Grid>
          <Grid item xs={12} md={6}>
            <Paper elevation={3} sx={{ padding: 2, bgcolor: '#2e2e2e' }}>
              <Typography variant="h6" sx={{ marginBottom: 1 }}>
                Price: {product.price.toLocaleString()} VND
              </Typography>
              <Typography variant="body1" sx={{ marginBottom: 1 }}>
                Description: {product.description}
              </Typography>
              <Typography variant="body2" sx={{ marginBottom: 1 }}>
                Stock: {product.stockQuantity}
              </Typography>
              <Button onClick={addToCart} variant="contained" color="primary" sx={{ marginTop: 2 }}>
                Add to Cart
              </Button>
            </Paper>
          </Grid>
        </Grid>
      </Box>
    </Scene>
  );
};

export default ProductDetail;