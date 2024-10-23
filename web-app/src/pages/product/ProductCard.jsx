import React from "react";
import { Card, CardContent, Typography, CardMedia, Button, Box } from "@mui/material";
import { useNavigate } from "react-router-dom";

const ProductCard = ({ product, onAddToCart, onBuyNow }) => {
  const navigate = useNavigate();

  return (
    <Card 
      sx={{ height: '100%', display: 'flex', flexDirection: 'column', bgcolor: '#2e2e2e', color: '#e0e0e0' }} 
      onClick={() => navigate(`/product/${product.id}`)} // Navigate on card click
    >
      <CardMedia
        component="img"
        height="200"
        image={product.image || 'https://via.placeholder.com/200'} // Fallback image
        alt={product.name}
        sx={{ objectFit: 'cover', cursor: 'pointer' }} // Add cursor pointer for image
      />
      <CardContent sx={{ flexGrow: 1, color: '#f5f5f5' }}>
        <Typography gutterBottom variant="h6" sx={{ color: '#f5f5f5' }}>
          {product.name}
        </Typography>
        <Typography variant="body2" sx={{ color: '#f5f5f5' }}>
          Price: {product.price} VND
        </Typography>
        <Typography variant="body2" sx={{ color: '#f5f5f5' }}>
          Stock: {product.stockQuantity}
        </Typography>
      </CardContent>
      <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between' }}>
        <Button 
          variant="contained" 
          color="primary" 
          onClick={(e) => { e.stopPropagation(); onAddToCart(product); }} // Prevent card click
        >
          Add to Cart
        </Button>
        <Button 
          variant="outlined" 
          color="secondary" 
          onClick={(e) => { e.stopPropagation(); onBuyNow(product); }} // Prevent card click
        >
          Buy Now
        </Button>
      </Box>
    </Card>
  );
};

export default ProductCard;