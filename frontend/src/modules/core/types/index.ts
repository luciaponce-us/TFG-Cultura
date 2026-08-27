export class ApiError extends Error {
  status: number;
  timestamp: string;
  errors: { [key: string]: string };

  constructor(
    message: string,
    status: number,
    errors: { [key: string]: string } = {},
  ) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.timestamp = new Date().toISOString();
    this.errors = errors;
  }
}

export interface Paginated<T> {
  totalElements: number;
  totalPages: number;
  pageable: {
    paged: boolean;
    pageNumber: number;
    pageSize: number;
    unpaged: boolean;
    offset: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
  };
  first: boolean;
  last: boolean;
  size: number;
  content: T[];
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  numberOfElements: number;
  empty: boolean;
}
