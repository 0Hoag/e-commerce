import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Button,
  TextField,
  Snackbar,
  Checkbox,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import Scene from '../Scene';
import { Link, useNavigate } from 'react-router-dom';
import cartItemService from '../../services/cartItemService';
import selectedProductService from '../../services/selectedProductService';
import ordersService from '../../services/ordersService';
import profileService from '../../services/profileService';

const ShoppingCart = () => {
  const [cartItems, setCartItems] = useState([]);
  const [selectedItems, setSelectedItems] = useState([]);
  const [selectedProducts, setSelectedProducts] = useState([]);
  const [snackBarOpen, setSnackBarOpen] = useState(false);
  const [snackBarMessage, setSnackBarMessage] = useState('');
  const [snackType, setSnackType] = useState('error');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState(null);

  const showError = (message) => {
    setSnackType('error');
    setSnackBarMessage(message);
    setSnackBarOpen(true);
  };

  const showSuccess = (message) => {
    setSnackType('success');
    setSnackBarMessage(message);
    setSnackBarOpen(true);
  };

  const calculateTotalPrice = () => {
    return selectedItems.reduce((total, itemId) => {
      const selectedItem = cartItems.find((item) => item.cartItemId === itemId);
      if (selectedItem && selectedItem.productId && selectedItem.productId.price) {
        return total + (selectedItem.productId.price * selectedItem.quantity);
      }
      return total;
    }, 0);
  };

  const handleQuantityChange = async (id, newQuantity) => {
    try {
      const updatedCart = cartItems.map((item) =>
        item.cartItemId === id ? { ...item, quantity: parseInt(newQuantity, 10) } : item
      );
      setCartItems(updatedCart);

      const selectedItem = updatedCart.find(item => item.cartItemId === id);
      if (selectedItem) {
        await cartItemService.updateCartItem(id, {
          productId: selectedItem.productId.id,
          quantity: selectedItem.quantity,
        });

        const selectedProduct = selectedProducts.find(product => product.productId.id === selectedItem.productId.id);
        if (selectedProduct) {
          await selectedProductService.updateSelectedProduct(selectedProduct.selectedId, {
            productId: selectedProduct.productId.id,
            quantity: selectedItem.quantity,
          });
        }

        showSuccess('Cập nhật số lượng sản phẩm thành công!');
      }
    } catch (error) {
      console.error('Error updating cart item quantity:', error);
      showError('Xảy ra lỗi khi cập nhật số lượng sản phẩm.');
    }
  };

  const handleRemoveFromCart = async (cartItem) => {
    try {
      const userInfo = await profileService.getMyProfile();
      const userId = userInfo.data.result.profileId;
      const orderId = userInfo.data.result.orders?.[0]?.orderId;
      const cartItemId = cartItem?.cartItemId;
  
      if (!cartItemId) {
        console.error('Missing cartItemId in cartItem:', cartItem);
        return;
      }
  
      await cartItemService.removeCartItem(userId, [cartItemId]);
  
      // Update selected products
      const selectedProductToRemove = selectedProducts.find((product) => product.productId.id === cartItem.productId.id);
      if (selectedProductToRemove) {
        await selectedProductService.removeSelectedProduct(orderId, [selectedProductToRemove.selectedId]);
        setSelectedProducts(selectedProducts.filter((product) => product !== selectedProductToRemove));
        setSelectedItems(selectedItems.filter((itemId) => itemId !== cartItemId));
      }
  
      const updatedCart = cartItems.filter((item) => item.cartItemId !== cartItemId);
      setCartItems(updatedCart);
      showSuccess('Xóa sản phẩm khỏi giỏ hàng thành công!');
    } catch (error) {
      console.error('Error removing item from cart:', error);
      showError('Xảy ra lỗi khi xóa sản phẩm khỏi giỏ hàng.');
    }
  };
  
  const handleSelectItem = async (itemId) => {
    try {
      const userInfo = await profileService.getMyProfile();
      const userId = userInfo.data.result.profileId;
      const selectedItem = cartItems.find((item) => item.cartItemId === itemId);
  
      if (!selectedItem) {
        showError('Sản phẩm không tìm thấy trong giỏ hàng.');
        return;
      }
  
      // Kiểm tra xem sản phẩm đã được chọn chưa
      if (selectedItems.includes(itemId)) {
        showError('Sản phẩm đã được chọn.');
        return;
      }
  
      const newSelectedProduct = {
        productId: selectedItem.productId.id,
        quantity: selectedItem.quantity,
        profileId: userId,
      };
  
      const createdProduct = await selectedProductService.createSelectedProduct(newSelectedProduct);
      const createdProductId = createdProduct.result.selectedId;
  
      setSelectedProducts([...selectedProducts, createdProductId]);
      setSelectedItems([...selectedItems, itemId]);
      showSuccess('Thêm sản phẩm vào danh sách chọn thành công!');
    } catch (error) {
      console.error('Error selecting item:', error);
      showError('Xảy ra lỗi khi chọn sản phẩm.');
    }
  };
  
  const handleCheckout = async () => {
    if (selectedItems.length === 0) {
      showError('Vui lòng chọn ít nhất một sản phẩm để thanh toán.');
      return;
    }

    try {
      const userInfo = await profileService.getMyProfile();
      const userId = userInfo.data.result.profileId;

      const order = {
        fullName: null,
        phoneNumber: null,
        country: null,
        city: null,
        district: null,
        ward: null,
        address: null,
        paymentMethod: null,
        vnpTxnRef: null,
        vnpOrderInfo: null,
        vnpAmount: 0,
        vnpResponseCode: null,
        vnpTransactionNo: null,
        vnpPayDate: null,
        vnpTransactionStatus: null,
        selectedProducts: selectedProducts,
        profileId: userId,
      };

      await ordersService.createOrders(order);
      showSuccess('Đơn hàng được tạo thành công!');
      
      // Cập nhật trạng thái giỏ hàng và các mục đã chọn
      const updatedCartItems = cartItems.filter((item) => !selectedItems.includes(item.cartItemId));
      setCartItems(updatedCartItems);
      setSelectedItems([]);
      setSelectedProducts([]);

      navigate('/checkout', {
        state: { selectedItems: selectedItems.map((id) => cartItems.find((item) => item.cartItemId === id)) },
      });
    } catch (error) {
      console.error('Error creating order:', error);
      showError('Xảy ra lỗi khi tạo đơn hàng.');
    }
  };

  const getUserDetails = async () => {
    try {
      const response = await profileService.getMyProfile();
      setUserDetails(response.data.result);
    } catch (error) {
      showError(error.message);
    }
  };

  useEffect(() => {
    const fetchUserDetails = async () => {
      try {
        await getUserDetails();
      } catch (error) {
        console.error('Error fetching user details:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchUserDetails();
  }, []);

  useEffect(() => {
    if (userDetails) {
      const cartItemsFromBackend = userDetails.cartItem || [];
      setCartItems(cartItemsFromBackend);

      if (selectedItems.length === 0 && selectedProducts.length === 0) {
        const selectedProducts = userDetails.orders?.[0]?.selectedProducts || [];
        const selectedProductIds = selectedProducts.map(product => product.productId.id);
        const initialSelectedItems = cartItemsFromBackend
          .filter(item => selectedProductIds.includes(item.productId.id))
          .map(item => item.cartItemId);
        setSelectedItems(initialSelectedItems);
        setSelectedProducts(selectedProducts);
      }
    }
  }, [userDetails]);

  useEffect(() => {
    if (snackBarOpen) {
      const timer = setTimeout(() => {
        setSnackBarOpen(false);
        setSnackBarMessage('');
      }, 3000);

      return () => clearTimeout(timer);
    }
  }, [snackBarOpen]);

  if (loading) {
    return <Scene><Box sx={{ p: 3 }}><Typography>Loading...</Typography></Box></Scene>;
  }

  return (
    <Scene>
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" gutterBottom>
          Giỏ hàng
        </Typography>
        {cartItems.length === 0 ? (
          <Typography variant="body1">Giỏ hàng của bạn trống.</Typography>
        ) : (
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell></TableCell>
                  <TableCell>Hình ảnh</TableCell>
                  <TableCell>Sản phẩm</TableCell>
                  <TableCell align="right">Đơn giá</TableCell>
                  <TableCell align="right">Số lượng</TableCell>
                  <TableCell align="right">Thành tiền</TableCell>
                  <TableCell align="right"></TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {cartItems.map((item) => (
                  <TableRow key={item.cartItemId}>
                    <TableCell>
                      <Checkbox
                        checked={selectedItems.includes(item.cartItemId)}
                        onChange={() => handleSelectItem(item.cartItemId)}
                      />
                    </TableCell>
                    <TableCell>
                    <Link to={`/books/${item.productId.id}`}> 
                    {/* it change url not /books/ */}
                      <img
                        src={item.productId.image.replace("?raw=1", "?dl=0")}
                        height="200"
                        style={{ cursor: 'pointer' }}
                      />
                    </Link>
                  </TableCell>
                    <TableCell>{item.productId.name}</TableCell>
                    <TableCell align="right">{item.productId.price} VND</TableCell>
                    <TableCell align="right">
                      <TextField
                        type="number"
                        value={item.quantity}
                        onChange={(e) => handleQuantityChange(item.cartItemId, e.target.value)}
                        inputProps={{ min: 1 }}
                        sx={{ width: '60px' }}
                      />
                    </TableCell>
                    <TableCell align="right">
                      {item.productId.price * item.quantity} VND
                    </TableCell>
                    <TableCell align="right">
                      <IconButton onClick={() => handleRemoveFromCart(item)}>
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
                <TableRow>
                  <TableCell colSpan={2}>
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={handleCheckout}
                    disabled={selectedItems.length === 0}
                  >
                    Thanh toán
                  </Button>
                  </TableCell>
                  <TableCell align="right" colSpan={2}>
                    <Typography variant="h6">Tổng tiền:</Typography>
                  </TableCell>
                  <TableCell align="right" colSpan={2}>
                    <Typography variant="h6">{calculateTotalPrice()} VND</Typography>
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Box>
      <Snackbar
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
        open={snackBarOpen}
        autoHideDuration={3000}
        onClose={() => setSnackBarOpen(false)}
        message={snackBarMessage}
        ContentProps={{
          sx: { backgroundColor: snackType === 'error' ? '#d32f2f' : '#43a047' },
        }}
      />
    </Scene>
  );
};

export default ShoppingCart;
