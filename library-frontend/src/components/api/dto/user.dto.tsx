import { UserRole } from './login.dto';

export class CreateUserDTO {
  username: string | undefined;
  password: string | undefined;
  role: UserRole | undefined;
  email: string | undefined;
}

export class ResponseUserDTO {
  id: number | undefined;
  password: string | undefined;
  role: UserRole | undefined;
}

export class UpdateUserDTO {
  username: string | undefined;
  currentPassword: string | undefined;
  newPassword: string | undefined;
}
