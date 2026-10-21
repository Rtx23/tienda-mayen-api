export interface Producto { id:number; nombre:string; precio:number; stock:number; stockMinimo:number; fechaVencimiento:string; }
export type ProductoInput = Omit<Producto,'id'>;
export interface Detalle { productoId:number; nombreProducto:string; cantidad:number; precioUnitario:number; subtotal:number; }
export interface Venta { id:number; fecha:string; usuario:string; total:number; detalles:Detalle[]; }
export interface Item { productoId:number; cantidad:number; }
export interface Session { accessToken:string; username:string; roles:string[]; expiresIn:number; expiresAt:number; }

