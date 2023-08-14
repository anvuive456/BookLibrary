package com.consoft.booklibrary.interfaces

interface FilterHandler<T> {
  fun onItemClicked(value: T)

}