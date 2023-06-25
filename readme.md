# Bloomify

Bloomify is a Java library that provides an implementation of a Bloom filter, a probabilistic data structure which is  
used to search an element within a large set of elements in constant time that is O(K) where K is the number of hash  
functions being used in Bloom Filter.

This is useful in cases where:
+ the data to be searched is large
+ the memory available on the system is limited/low

There are numerous scenarios where a bloom filter can be effectively utilized, such as weak password detection. The 
concept revolves around the system maintaining a Bloom Filter that comprises a list of weak passwords.

It is also useful to enhancing user experience by checking username availability in real-time, you can provide immediate 
feedback to users during the registration process. If an username is already taken, you can display an error message or 
suggestion to choose a different one, saving users the time and frustration of submitting a form only to find out later 
that their preferred username is already in use.

The only thing to note is that this is a probabilistic data structure so for a small number of cases, it may give wrong
results (which can be limited).

### Features

+ Efficient and memory-optimized Bloom filter implementation.
+ Configurable expected insertions and false positive rate.
+ Supports adding individual elements to the Bloom filter.
+ Supports bulk insertion of multiple elements with parallel processing.

## Usage

- Create an instance of Bloomify by specifying the expected number of insertions and the desired false positive rate:

```
int expectedInsertions = 1000;
double falsePositiveRate = 0.01;
Bloomify bloomify = new Bloomify(expectedInsertions, falsePositiveRate);
```
- Use the contains() method to check if an element is likely to be a member of the set:

```
String element = "example";
boolean isMember = bloomify.contains(element);
```
- Add elements to the Bloom filter using the add() method:

```
String newElement = "newexample";
bloomify.add(newElement);
```

- Optionally, you can provide a custom StorageStrategy implementation during the initialization of Bloomify to define the storage mechanism for the Bloom filter. The library comes with a default LocalStorage strategy.

```
StorageStrategy customStrategy = new CustomStorageStrategy();
Bloomify bloomify = new Bloomify(expectedInsertions, falsePositiveRate, customStrategy);
```

### Example implementation of the StorageStrategy interface using Redis as the storage mechanism:

```
import redis.clients.jedis.Jedis;

public class RedisStorage implements StorageStrategy {
  private final Jedis jedis;
  private final String bloomFilterKey;

  public RedisStorage(String host, int port, String bloomFilterKey) {
    this.jedis = new Jedis(host, port);
    this.bloomFilterKey = bloomFilterKey;
  }

  @Override
  public boolean contains(int element) {
    return jedis.getbit(bloomFilterKey, element);
  }

  @Override
  public void add(int hashCode) {
    jedis.setbit(bloomFilterKey, hashCode, true);
  }
}
```

### Performance Considerations

The Bloomify library provides efficient and memory-optimized operations. However, it's important to consider the following points:
+ The choice of expected insertions and false positive rate impacts the memory consumption and the accuracy of the Bloom filter. Adjust these parameters based on your specific requirements.
+ Bulk insertion using the addAll method with parallel processing can significantly improve performance when inserting a large number of elements. Adjust the thread pool size and chunk size to optimize the parallel processing based on your system's capabilities.
+ Keep in mind that the Bloom filter is a probabilistic data structure, and false positives are possible. The false positive rate increases as the Bloom filter approaches its capacity.

### License

This project is licensed under the MIT License.

Feel free to contribute to the project by opening issues or submitting pull requests on the GitHub repository.

### Contributing

Contributions to Bloomify are welcome! If you find any issues, have suggestions for improvements, or would like to add new features, please create a pull request or open an issue on the GitHub repository.

### Acknowledgments

Bloomify was developed by Fabiano Armando as a showcase of skills and to provide a useful tool for others. Thank you to the open-source community for the inspiration and valuable resources.

### References

+ [Bloom Filter - Wikipedia](https://en.wikipedia.org/wiki/Bloom_filter) 
+ [Burton H. Bloom. "Space/Time Trade-offs in Hash Coding with allowable errors
  ](https://dl.acm.org/doi/10.1145/362686.362692)

