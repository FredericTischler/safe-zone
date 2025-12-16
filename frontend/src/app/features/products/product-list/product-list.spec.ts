import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

import { ProductList } from './product-list';
import { Product as ProductService } from '../../../core/services/product';
import { MediaService } from '../../../core/services/media';
import { Cart } from '../../../core/services/cart';
import { Auth } from '../../../core/services/auth';
import { Product as ProductModel } from '../../../core/models/product.model';
import { Media } from '../../../core/models/media.model';
import { CartItem } from '../../../core/models/cart.model';

describe('ProductList', () => {
  let component: ProductList;
  let fixture: ComponentFixture<ProductList>;
  let productService: jasmine.SpyObj<ProductService>;
  let mediaService: jasmine.SpyObj<MediaService>;
  let cartService: Cart;
  let authService: jasmine.SpyObj<Auth>;
  let snackBar: jasmine.SpyObj<MatSnackBar>;
  let router: Router;
  let cartStream: BehaviorSubject<any[]>;

  beforeEach(async () => {
    productService = jasmine.createSpyObj<ProductService>('Product', ['getAllProducts', 'searchProducts']);
    mediaService = jasmine.createSpyObj<MediaService>('MediaService', ['getMediaByProduct', 'getImageUrl']);
    authService = jasmine.createSpyObj<Auth>('Auth', ['logout']);
    snackBar = jasmine.createSpyObj<MatSnackBar>('MatSnackBar', ['open']);
    cartStream = new BehaviorSubject<any[]>([]);
    cartService = {
      cartItems$: cartStream.asObservable(),
      getCartCount: () => 0,
      getCartItems: () => [],
      addToCart: jasmine.createSpy('addToCart'),
    } as unknown as Cart;

    snackBar.open.and.returnValue({
      onAction: () => of(null),
    } as any);
    productService.getAllProducts.and.returnValue(of([]));
    mediaService.getMediaByProduct.and.returnValue(of([]));
    mediaService.getImageUrl.and.callFake((url: string) => url);

    await TestBed.configureTestingModule({
      imports: [
        ProductList,
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([]),
        NoopAnimationsModule,
      ],
      providers: [
        { provide: ProductService, useValue: productService },
        { provide: MediaService, useValue: mediaService },
        { provide: Cart, useValue: cartService },
        { provide: Auth, useValue: authService },
        { provide: MatSnackBar, useValue: snackBar },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ProductList);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load products with images', () => {
    const product: ProductModel = {
      id: '1',
      name: 'Phone',
      description: 'Flagship',
      price: 100,
      category: 'Tech',
      stock: 5,
      sellerId: 'seller',
      sellerName: 'Seller',
    };
    const media: Media = {
      id: 'm1',
      productId: '1',
      filename: 'image.png',
      contentType: 'image/png',
      size: 1000,
      uploadedBy: 'seller',
      url: '/image.png',
      uploadedAt: new Date(),
    };
    productService.getAllProducts.and.returnValue(of([product]));
    mediaService.getMediaByProduct.and.returnValue(of([media]));
    mediaService.getImageUrl.and.returnValue('http://cdn/image.png');

    component.loadProducts();

    expect(component.products[0].imageUrl).toBe('http://cdn/image.png');
    expect(component.loading).toBeFalse();
  });

  it('should handle search with empty keyword', () => {
    spyOn(component, 'loadProducts');
    component.searchKeyword = '   ';
    component.onSearch();
    expect(component.loadProducts).toHaveBeenCalled();
  });

  it('should search products and handle errors', () => {
    component.searchKeyword = 'phone';
    const product: ProductModel = {
      id: '1',
      name: 'Phone',
      description: 'Flagship',
      price: 100,
      category: 'Tech',
      stock: 5,
      sellerId: 'seller',
      sellerName: 'Seller',
    };
    productService.searchProducts.and.returnValue(of([product]));
    mediaService.getMediaByProduct.and.returnValue(throwError(() => new Error('media error')));

    component.onSearch();

    expect(component.products.length).toBe(1);
    expect(component.loading).toBeFalse();
  });

  it('should prevent adding out-of-stock items', () => {
    component.addToCart({ id: '1', name: 'Phone', stock: 0 });
    expect((cartService.addToCart as jasmine.Spy).calls.count()).toBe(0);
  });

  it('should prevent adding beyond available stock', () => {
    const cartItem: CartItem = {
      productId: '1',
      quantity: 2,
      name: 'Phone',
      price: 100,
      imageUrl: null,
    };
    const getCartItemsSpy = spyOn(cartService, 'getCartItems').and.returnValue([cartItem]);
    component.addToCart({ id: '1', name: 'Phone', stock: 2 });
    expect(getCartItemsSpy).toHaveBeenCalled();
    expect((cartService.addToCart as jasmine.Spy).calls.count()).toBe(0);
  });

  it('should add to cart and offer navigation', () => {
    component.addToCart({ id: '1', name: 'Phone', stock: 5, price: 100 });
    expect((cartService.addToCart as jasmine.Spy).calls.any()).toBeTrue();
  });

  it('should logout and redirect', () => {
    component.logout();
    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should navigate to product details and cart', () => {
    component.viewDetails('1');
    expect(router.navigate).toHaveBeenCalledWith(['/products', '1']);

    component.goToCart();
    expect(router.navigate).toHaveBeenCalledWith(['/cart']);
  });
});
